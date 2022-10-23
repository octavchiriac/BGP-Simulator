package runner;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import components.Globals;
import components.NeighborTable;
import components.Router;
import multithread.SendTcpPacket;
import multithread.ThreadPool;
import utils.ParseInputFile;

import static components.Globals.linkMap;
import static components.Globals.routers;

public class DoTest {

    private static void establishTcpConnection(String ipAddress1, String ipAddress2) throws InterruptedException {

            // Send SYN message
            SendTcpPacket task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0,
                    ipAddress1, ipAddress2, Globals.DESTINATION_MAC_ADDRESS, true, false);
            ThreadPool.submit(task);

            Thread.sleep(1000);

            // Send SYN + ACK message
            task = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1,
                    ipAddress2, ipAddress1, Globals.DESTINATION_MAC_ADDRESS, true, true);
            ThreadPool.submit(task);

            Thread.sleep(1000);

            // Send ACK message
            task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1,
                    ipAddress1, ipAddress2, Globals.DESTINATION_MAC_ADDRESS, false, true);
            ThreadPool.submit(task);

            Thread.sleep(1000);

            Router r1 = Router.getRouterByIP(ipAddress1);
            Router r2 = Router.getRouterByIP(ipAddress2);

            r1.addTcpConnectedRouter(r2);
            r2.addTcpConnectedRouter(r1);
    }

    private static void changeRouterStateFromInput() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter router to be shut down followed by desired state: ");
        boolean wrongInput = true;
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
                }
            }

            if(wrongInput)
                System.err.println("No router found with this name. Please try again or type \"exit\" to skip");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        // Parse input file
        ParseInputFile parseInput = new ParseInputFile();
        parseInput.parseRouterInterfaces();
        parseInput.parseDirectLinks();
//
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

        ThreadPool.stop();

        // Select router to be shut down
        changeRouterStateFromInput();

    }
}
