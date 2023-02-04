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
import utils.IpFunctions;
import utils.ParseInputFile;

import static components.Globals.*;
import static components.Router.getRouterByIP;

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
    private static Router customizeRoutingTable() throws InterruptedException {
        Router changedRouter = null;
        Scanner input = new Scanner(System.in);

        System.out.println("Enter router name followed by desired entry " +
                "<DESTINATION_IP AS1,AS2,..ASn NEXT_HOP> : ");

        String line = input.nextLine();
        synchronized (Globals.lock) {
            String routerName = "";
            String tableEntry = "";
            try {
                routerName = line.substring(0, line.indexOf(" "));
                tableEntry = line.substring(line.indexOf(" ") + 1);
            } catch (Exception e) {
                System.err.println("Wrong input format. Please try again or type \"exit\" to skip");
            }

            if (Globals.routerNames.contains(routerName)) {
                Router r = Router.getRouterByName(routerName);
                changedRouter = r;

                String destinationIp = tableEntry.split(" ")[0];
                String asList = tableEntry.split(" ")[1];
                String nextHop = tableEntry.split(" ")[2];
                String[] asArray = asList.split(",");

                String[] asIpArray = new String[asArray.length];
                for (int i = 0; i < asArray.length; i++) {
                    asIpArray[i] = IpFunctions.getIpFromAs(asArray[i]);
                }

                System.out.println("destinationIp: " + destinationIp + " asList: " + asList + " nextHop: " + nextHop + " asIpArray: " + Arrays.toString(asIpArray));

                BGPRoutingTable topologyTable = r.getRoutingTable();

                topologyTable.insertNewEntry(destinationIp, "0.0.0.0",
                        ArrayUtils.toArray(new PathSegments(destinationIp, asIpArray)), nextHop, 0);
                System.out.println("Entry added to routing table of " + r.getName());
                Thread.sleep(1000);
                System.out.println("Propagating new information to neighbors...");
                // Propagate new information to neighbors of this router
                r.getNeighborTable().getNeighborInfo().forEach(key -> {
                    System.out.println("[" + r.getName() + " -> " + key.getIp() +
                            "] Forwarding custom UPDATE to neighbor " + key.getIp()
                            + " @ " + Objects.requireNonNull(getRouterByIP(key.getIp())).getName());
                    sendUpdateMessage(r, key.getIp());
                });
            }
        }

        System.out.println("Route added and information propagated!");

        return changedRouter;
    }

    private static void sendUpdateMessage(Router r, String destinationIp) {
        //get all the interfaces for the router
        ArrayList<RouterInterface> interfaces = r.getInterfaces();
        //send the update for each interface of the selected router
        for (RouterInterface in : interfaces) {
            NeighborTable tmpNeighborTable = r.getNeighborTable();
            //get all the IP addresses of the neighbors
            ArrayList<String> neighborIPs = tmpNeighborTable.getNeighborIPs();

            List<Map<Integer, String>> WithdrawnRoutes = new ArrayList<>();
            List<Map<Integer, String>> NetworkLayerReachabilityInformation = new ArrayList<>();
            PathAttributes pathAttributes;

            //get the IP address of the interface and use it as source IP
            String sourceIP = in.getIpAddress();

            System.out.println("Sending update message from " + sourceIP);

            // filling lists with random data
            Map<Integer, String> WithdrawnRoute;
            for (int i = 2; i < 3; i++) {
                WithdrawnRoute = new HashMap<>();
                WithdrawnRoute.put(i, "100.0.0." + i);
                WithdrawnRoutes.add(WithdrawnRoute);
            }

            Map<Integer, String> NetworkLayerReachabilityInfo;
            // get the IP addresses of the neighbors and put it in the NLRI
            for (int i = 0; i < neighborIPs.size(); i++) {
                NetworkLayerReachabilityInfo = new HashMap<>();
                NetworkLayerReachabilityInfo.put(i, neighborIPs.get(i));
                NetworkLayerReachabilityInformation.add(NetworkLayerReachabilityInfo);
            }

            // creating path attributes for AS_PATH field
            String[] pathSegmentsVal = new String[1];
            pathSegmentsVal[0] = sourceIP;
            PathSegments ps = new PathSegments("0.0.0.0", pathSegmentsVal);
            PathSegments[] psList = new PathSegments[1];
            psList[0] = ps;

            pathAttributes = new PathAttributes("1", psList, sourceIP, 0);

            SendUpdateMessage task = new SendUpdateMessage(sourceIP, destinationIp, WithdrawnRoutes, pathAttributes, NetworkLayerReachabilityInformation);
            ThreadPool.submit(task);
        }
    }

    private static void exchangeTrustBetweenRouters(Router r, String destinationIp) {
        //get all the interfaces for the router
        ArrayList<RouterInterface> interfaces = r.getInterfaces();
        //send the update for each interface of the selected router
        for (RouterInterface in : interfaces) {
            NeighborTable tmpNeighborTable = r.getNeighborTable();
            //get all the IP addresses of the neighbors
            ArrayList<String> neighborIPs = tmpNeighborTable.getNeighborIPs();

            List<Map<Integer, String>> WithdrawnRoutes = new ArrayList<>();
            List<Map<Integer, String>> NetworkLayerReachabilityInformation = new ArrayList<>();
            PathAttributes pathAttributes;

            //get the IP address of the interface and use it as source IP
            String sourceIP = in.getIpAddress();

            System.out.println("Sending update message from " + sourceIP);

            // filling lists with random data
            Map<Integer, String> WithdrawnRoute;
            for (int i = 2; i < 3; i++) {
                WithdrawnRoute = new HashMap<>();
                WithdrawnRoute.put(i, "100.0.0." + i);
                WithdrawnRoutes.add(WithdrawnRoute);
            }

            Map<Integer, String> NetworkLayerReachabilityInfo;
            // get the IP addresses of the neighbors and put it in the NLRI
            for (int i = 0; i < neighborIPs.size(); i++) {
                NetworkLayerReachabilityInfo = new HashMap<>();
                NetworkLayerReachabilityInfo.put(i, neighborIPs.get(i));
                NetworkLayerReachabilityInformation.add(NetworkLayerReachabilityInfo);
            }

            // creating path attributes for AS_PATH field
            String[] pathSegmentsVal = new String[1];
            pathSegmentsVal[0] = sourceIP;
            PathSegments ps = new PathSegments("0.0.0.0", pathSegmentsVal);
            PathSegments[] psList = new PathSegments[1];
            psList[0] = ps;

            pathAttributes = new PathAttributes("1", psList, sourceIP, 0);

            SendUpdateMessage task = new SendUpdateMessage(sourceIP, destinationIp, WithdrawnRoutes, pathAttributes, NetworkLayerReachabilityInformation);
            ThreadPool.submit(task);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

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

        // Select router to change state
        Thread.sleep(2000);
        Router shutdownRouter = changeRouterStateFromInput();

        fullMap.entrySet().parallelStream().forEach(entry -> {
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
            fullMap.entrySet().parallelStream().forEach(entry -> {
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

        Thread.sleep(30000);

        // Send update messages to neighbors
        AtomicInteger counter = new AtomicInteger();
        linkMap.entrySet().parallelStream().forEach(entry -> {
            // send just one update pkt per router
            if (counter.get() == 0) {
                //for each router take their neighbor table and send update message to all neighbors
                for (Router r : routers) {
                    sendUpdateMessage(r, (String) entry.getValue());
                    counter.getAndIncrement();
                }
            }
        });

        // Customize routing table
        Thread.sleep(15000);
        Router changedRouter = customizeRoutingTable();

        // Print customized routing table
        Thread.sleep(15000);
        changedRouter.printRoutingTable();

        // Calculate direct trust between routers and exchange
        fullMap.entrySet().parallelStream().forEach(entry -> {
            double votingCoefficient = 0;
            double directTrust = 0;
            double totalTrust;
            Router r2 = Router.getRouterByIP((String) entry.getValue());

            NeighborTable tmpNeighborTable = r2.getNeighborTable();
            //get all the IP addresses of the neighbors
            ArrayList<String> neighborIPs = tmpNeighborTable.getNeighborIPs();

            // calculate voting coefficient from neighbors (1/T1 + 1/T2 + ...)
            for (String ip : neighborIPs) {
                if(!ip.equals(entry.getKey())) {
                    votingCoefficient += 1 / tmpNeighborTable.getNeighborTrustByIp(ip);
                } else {
                    directTrust = tmpNeighborTable.getNeighborTrustByIp(ip);
                }
            }

            /*
             * calculate recommendation coefficient by formula (1 + votingCoefficient) * 1/directTrust, as described in
             * HYBRID TRUST MODEL FOR INTERNET ROUTING (Pekka Rantala, Seppo Virtanen and Jouni Isoaho)
             */
            totalTrust = (1 + votingCoefficient) * (1 / directTrust);

            SendTrustExchangeMessage task = new SendTrustExchangeMessage(entry.getKey(), (String) entry.getValue(), totalTrust);
            ThreadPool.submit(task);
        });

        Thread.sleep(10000);

        // Exchange trust information between neighbors using WithdrawnRoutes parameter
        fullMap.entrySet().parallelStream().forEach(entry -> {
            Router r1 = Router.getRouterByIP(entry.getKey());
            Map<Integer, String> trustMap = new HashMap<>();

            for (Map.Entry<String, PathAttributes> row : r1.getRoutingTable().getBestRoutes().entrySet()) {

                //convert trust to integer, so it can use the same format
                int trustInt = (int) (row.getValue().getTRUSTRATE() * 1000);

                if (trustInt != 0) {
                    trustMap.put(trustInt, row.getKey());
                }
            }

            SendTrustListMessage task = new SendTrustListMessage(entry.getKey(), (String) entry.getValue(), trustMap);
            ThreadPool.submit(task);
        });
    }
}
