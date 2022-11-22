package runner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import components.*;
import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;
import multithread.*;
import org.apache.commons.lang3.ArrayUtils;
import utils.ParseInputFile;

import static components.Globals.linkMap;
import static components.Globals.routers;

public class DoTest {

    public static void establishTcpConnection(String ipAddress1, String ipAddress2) throws InterruptedException {
        Router r1 = Router.getRouterByIP(ipAddress1);
        RouterInterface i1 = r1.getRouterInterfaceByIP(ipAddress1);

        Router r2 = Router.getRouterByIP(ipAddress2);
        RouterInterface i2 = r2.getRouterInterfaceByIP(ipAddress2);

        if ((!i1.getState().equals(BGPStates.Active) ||
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
            System.out.println("\033[0;35m" + "[" + r1.getName() + " - " + i1.getName() + "] BGP state : OpenSent" + "\033[0m");
            i2.setState(BGPStates.OpenSent);
            System.out.println("\033[0;35m" + "[" + r2.getName() + " - " + i2.getName() + "] BGP state : OpenSent" + "\033[0m");
        }
    }

    private static Router changeRouterStateFromInput() throws InterruptedException {
        boolean wrongInput = true;
        Router changedRouter = null;
        Scanner input = new Scanner(System.in);

        System.out.println("Enter router name followed by desired state: ");

        while (wrongInput) {
            String line = input.nextLine();

            if (line.equals("exit")) {
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

            if (wrongInput) {
                System.err.println("No router found with this name. Please try again or type \"exit\" to skip");
            }
        }
        return changedRouter;
    }

    /* It is more difficult to do more modifications...will try later if we have time
     * Insert following lines to use and verify method
     *  Router changedRouter = addEntryInRoutingTable();
     *  changedRouter.getTopologyTable().printTable();
     */
    private static Router addEntryInRoutingTable() {
        boolean wrongInput = true;
        Router changedRouter = null;
        Scanner input = new Scanner(System.in);

        System.out.println("Enter router name followed by desired entry " +
                "<DESTINATION_IP AS1,AS2,..ASn NEXT_HOP> : ");

        while (wrongInput) {
            String line = input.nextLine();
            if (line.equals("exit")) {
                wrongInput = false;
            } else {
                    String routerName = line.substring(0, line.indexOf(" "));
                    String tableEntry = line.substring(line.indexOf(" ") + 1);

                    if (Globals.routerNames.contains(routerName)) {
                        wrongInput = false;
                        Router r = Router.getRouterByName(routerName);
                        changedRouter = r;

                        String destinationIp = tableEntry.split(" ")[0];
                        String asList = tableEntry.split(" ")[1];
                        String nextHop = tableEntry.split(" ")[2];
                        String[] asArray = asList.split(",");

                        TopologyTable topologyTable = r.getTopologyTable();

                        topologyTable.insertNewEntry("0.0.0.0",
                                ArrayUtils.toArray(new PathSegments(destinationIp, asArray)), nextHop);
                    }
                }

            if (wrongInput) {
                System.err.println("No router found with this name. Please try again or type \"exit\" to skip");
            } else {
                System.out.println("Route added!");
            }
        }
        return changedRouter;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        // Parse input file
        ParseInputFile parseInput = new ParseInputFile();
        parseInput.parseRouterInterfaces();
        parseInput.parseDirectLinks();

        for (int i = 0; i < routers.size(); i++) {
            Globals.routers.get(i).printRouterInfo();
        }

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
        

        AtomicInteger leonardo = new AtomicInteger();
        linkMap.entrySet().parallelStream().forEach(entry -> {
            if(leonardo.get() ==0) {
                List<Map<Integer, String>> WithdrawnRoutes = new ArrayList<>();
                List<Map<Integer, String>> NetworkLayerReachabilityInformation = new ArrayList<>();
                PathAttributes pathAttributes;

                // filling lists with random data
                Map<Integer, String> WithdrawnRoute = null;
                for (int i = 0; i < 3; i++) {
                    WithdrawnRoute = new HashMap<>();
                    WithdrawnRoute.put(i, "192.168.0." + i);
                    WithdrawnRoutes.add(WithdrawnRoute);
                }

                Map<Integer, String> NetworkLayerReachabilityInfo = null;
                for (int i = 0; i < 3; i++) {
                    NetworkLayerReachabilityInfo = new HashMap<>();
                    NetworkLayerReachabilityInfo.put(i, "122.168.0." + i);
                    NetworkLayerReachabilityInformation.add(NetworkLayerReachabilityInfo);
                }

                // creating path attributes for AS_PATH field
                String[] pathSegmentsVal = new String[3];
                for (int i = 0; i < 3; i++) {
                    pathSegmentsVal[i] = "192.198.0." + i;
                }
                PathSegments ps = new PathSegments("192.168.178.2", pathSegmentsVal);
                PathSegments[] psList = new PathSegments[1];
                psList[0] = ps;

                pathAttributes = new PathAttributes("0", psList, "10.0.0.2");


                System.out.println("Sending update message");


                SendUpdateMessage task = new SendUpdateMessage("10.0.0.1", (String) entry.getValue(), WithdrawnRoutes, pathAttributes, NetworkLayerReachabilityInformation);
                ThreadPool.submit(task);
                leonardo.getAndIncrement();
            }
        });


        // Select router to change state
        Thread.sleep(2000);
        Router shutdownRouter = changeRouterStateFromInput();

        linkMap.entrySet().parallelStream().forEach(entry -> {
            if (shutdownRouter.getEnabledInterfacesAddresses().contains(entry.getKey())) {
                SendNotificationMessage task1 =
                        new SendNotificationMessage(entry.getKey(), (String) entry.getValue());
                ThreadPool.submit(task1);
            }
        });

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
                        Thread.sleep(7000);
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
