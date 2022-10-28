package runner;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

import components.*;
import multithread.SendKeepAliveMessage;
import multithread.SendOpenMessage;
import multithread.SendTcpPacket;
import multithread.ThreadPool;
import utils.ParseInputFile;

import static components.Globals.linkMap;
import static components.Globals.routers;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.toList;

public class DoTest {

    public static void establishTcpConnection(String ipAddress1, String ipAddress2) throws InterruptedException {
        Router r1 = Router.getRouterByIP(ipAddress1);
        RouterInterface i1 = r1.getRouterInterfaceByIP(ipAddress1);

        Router r2 = Router.getRouterByIP(ipAddress2);
        RouterInterface i2 = r2.getRouterInterfaceByIP(ipAddress2);

        if((!i1.getState().equals(BGPStates.Active) ||
                i1.getState().equals(BGPStates.Idle)) &&
                    (!i2.getState().equals(BGPStates.Active) ||
                        i2.getState().equals(BGPStates.Idle))) {
            // Send SYN message
            SendTcpPacket task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0,
                    ipAddress1, ipAddress2, Globals.DESTINATION_MAC_ADDRESS, true, false, false, false, "");
            ThreadPool.submit(task);

            Thread.sleep(1000);

            // Send SYN + ACK message
            task = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1,
                    ipAddress2, ipAddress1, Globals.DESTINATION_MAC_ADDRESS, true, true, false, false, "");
            ThreadPool.submit(task);

            Thread.sleep(1000);

            // Send ACK message
            task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1,
                    ipAddress1, ipAddress2, Globals.DESTINATION_MAC_ADDRESS, false, true, false, false, "");
            ThreadPool.submit(task);

            Thread.sleep(1000);

            r1.addTcpConnectedRouter(r2);
            r2.addTcpConnectedRouter(r1);

            // Change BGP state to OpenSent
            i1.setState(BGPStates.OpenSent);
            System.out.println("\033[0;35m" + "[" + r1.getName()  + " - " + i1.getName() + "] BGP state : OpenSent" + "\033[0m");
            i2.setState(BGPStates.OpenSent);
            System.out.println("\033[0;35m" + "[" + r2.getName()  + " - " + i2.getName() + "] BGP state : OpenSent" + "\033[0m");
        }
    }

    private static Router changeRouterStateFromInput() throws InterruptedException {
        boolean wrongInput = true;
        Router changedRouter = null;
        Scanner input = new Scanner(System.in);

        System.out.println("Enter router name followed by desired state: ");

        while(wrongInput) {
            String line = input.nextLine();

            if(line.equals("exit")) {
                wrongInput = false;
            } else {
                String routerName = line.split(" ")[0];
                boolean routerState = line.split(" ")[1]
                        .equalsIgnoreCase("enabled") ? true : false;

                if (Globals.routerNames.contains(routerName)) {
                    wrongInput = false;
                    Router r = Router.getRouterByName(routerName);
                    r.setEnabled(routerState);
                    changedRouter = r;
                }
            }

            if(wrongInput) {
                System.err.println("No router found with this name. Please try again or type \"exit\" to skip");
            }
        }
        return changedRouter;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        // Parse input file
        ParseInputFile parseInput = new ParseInputFile();
        parseInput.parseRouterInterfaces();
        parseInput.parseDirectLinks();

//        for (int i = 0; i < 3; i++) {
//            Globals.routers.get(i).printRouterInfo();
//        }

        // Start up routers
        for (Router r : routers) {
            Thread t = new Thread(r);
            t.start();
        }

        // Wait for routers to finish "cold start"
        while (Globals.nrRoutersStarted != routers.size()) {
            Thread.sleep(1000);
        }

        // Establish TCP connections for every direct link in the neighbor table in parallel
        ThreadPool.run();

        linkMap.entrySet().parallelStream().forEach(entry -> {
            try {
                establishTcpConnection(entry.getKey(), (String) entry.getValue());
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        });

        linkMap.entrySet().parallelStream().forEach(entry -> {
            SendOpenMessage task = new SendOpenMessage(entry.getKey(), (String) entry.getValue());
            ThreadPool.submit(task);
        });


        // Send keep alive messages to every connected router
        linkMap.entrySet().parallelStream().forEach(entry -> {
            SendKeepAliveMessage task = new SendKeepAliveMessage(entry.getKey(), (String) entry.getValue());
            ThreadPool.submit(task);
        });

        // Select router to change state
        Thread.sleep(2000);
        changeRouterStateFromInput();

        // Select router to change state
        Router restartedRouter = changeRouterStateFromInput();


        if (restartedRouter != null) {
            // Restart router thread
            Thread t = new Thread(restartedRouter);
            t.start();

            Thread.sleep(1000);

            // Send RST message to previously connected routers
            linkMap.entrySet().parallelStream().forEach(entry -> {
                if (restartedRouter.getEnabledInterfacesAddresses().contains(entry.getKey())) {
                    SendTcpPacket task1 = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0,
                            entry.getKey(), (String) entry.getValue(), Globals.DESTINATION_MAC_ADDRESS,
                            false, false, false, true, "");
                    ThreadPool.submit(task1);

                    try {
                        // Resend OPEN message to previously connected routers
                        Thread.sleep(3000);
                        SendOpenMessage task2 = new SendOpenMessage(entry.getKey(), (String) entry.getValue());
                        ThreadPool.submit(task2);

                        // Resend KEEPALIVE message to previously connected routers
                        Thread.sleep(3000);
                        SendKeepAliveMessage task3 = new SendKeepAliveMessage(entry.getKey(), (String) entry.getValue());
                        ThreadPool.submit(task3);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }


        // TODO continue here

//        ThreadPool.stop();
    }
}
