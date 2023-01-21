package multithread;

import components.*;
import components.tblentries.NeighborTableEntry;
import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;
import packets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static components.Router.getRouterByIP;
import static runner.DoTest.establishTcpConnection;

public class ReceiveTcpPacket implements Runnable {
    String bitArrayHdlc;


    public ReceiveTcpPacket(String bitArrayHdlc) {
        super();
        this.bitArrayHdlc = bitArrayHdlc;
    }

    public void receiveTcpPacket(String bitArrayHdlc) throws Exception {
        boolean isFound = false;
        String interfaceName = "";

        Packet hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
        IpPacket ipPacket2 = new IpPacket(hdlcPacket2.getData());
        TcpPacket tcpPacket2 = new TcpPacket(ipPacket2.getData());

        String destinationIpAddress = ipPacket2.getDestinationAddress();
        Router dest = getRouterByIP(destinationIpAddress);
        RouterInterface destInt = dest.getRouterInterfaceByIP(destinationIpAddress);
        String destRouterName = dest.getName();

        String sourceIpAddress = ipPacket2.getSourceAddress();
        Router src = getRouterByIP(sourceIpAddress);
        RouterInterface srcInt = src.getRouterInterfaceByIP(sourceIpAddress);
        String srcRouterName = src.getName();

        if (ipPacket2.getTimeToLive() == 0) {
            throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] TTL expired. Packet dropped!");
        } else {
            // verify that destination ip address is one of the router's interfaces
            for (RouterInterface inter : dest.getEnabledInterfaces()) {
                if (inter.getIpAddress().equals(destinationIpAddress)) {
                    isFound = true;
                    interfaceName = inter.getName();
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] Destination address is matched by interface " + interfaceName);
                    break;
                }
            }
            if (!isFound) {
                System.err.println("[" + srcRouterName + " -> " + destRouterName + "] Destination address " + destinationIpAddress + " is NOT matched any interface");

                //TODO search in table for destination

                //TODO what to do here if you don't find the router in the table? do you broadcast? --> DROP THE PACKET (has this been implemented yet? @octavchiriac)
                ipPacket2.decreaseTimeToLive();

                Packet hdlcPacket = new HdlcPacket("01111110", Globals.DESTINATION_MAC_ADDRESS, "00000000", ipPacket2.packetToBitArray(), "00000000");

                SendTcpPacket task = new SendTcpPacket(hdlcPacket.packetToBitArray(), ipPacket2.getSourceAddress(),
                        ipPacket2.getDestinationAddress(), tcpPacket2.isSyn(), tcpPacket2.isAck(),
                        tcpPacket2.isPsh(), tcpPacket2.isRst(), tcpPacket2.getData());
                ThreadPool.submit(task);

            } else {
                /** Here you need to add another if for the UPDATE packets, and somehow differentiate it from the OPEN one,
                 * Problem is that both OPEN and KEEPALIVE and UPDATE packets have the same bits on true (ACK + PSH)
                 * So maybe try to differentiate from the first bit of the data encapsulated as BGP packet(?)
                 * For example, every OPEN packet starts with the BGP version which is 4, so every packet starts like
                 * 0000010.... -> every 6th bit is 1....you can see how update packets will look like in binary
                 * and maybe try doing this to differentiate
                 *
                 * Done --> I set the 6th bit as 1 to differentiate the UPDATE packets from the other ones
                 * */

                // Receiving KEEPALIVE packet
                if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().length() == 0) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] KEEPALIVE packet sucessfully received on interface " + interfaceName);

                    if (tcpPacket2.getSequenceNumber() == 0 || tcpPacket2.getSequenceNumber() == 1) {
                        destInt.setState(BGPStates.Established);
                        System.out.println("\033[0;35m" + "[" + dest.getName() + " - " + destInt.getName() + "] BGP state : Established" + "\033[0m");
                    }
                }

                // Receiving NOTIFICATION packet
                else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(5) == '1' && tcpPacket2.getData().charAt(6) == '1') {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] NOTIFICATION packet sucessfully received on interface " + interfaceName);

                    // Change BGP state to OpenConfirm
                    destInt.setState(BGPStates.Connect);
                    System.out.println("\033[0;35m" + "[" + dest.getName() + " - " + destInt.getName() + "] BGP state : Connect" + "\033[0m");
                }

                // Receiving TRUSTRATE packet
                else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(5) == '1' && tcpPacket2.getData().charAt(7) == '1') {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] TRUSTRATE packet sucessfully received on interface " + interfaceName);

                    TrustMessagePacket bgpPacket;
                    try {
                        String stringedPkt = tcpPacket2.getData().substring(8); // remove the header of first 8 bits
                        bgpPacket = new TrustMessagePacket(stringedPkt);
                    } catch (Exception e) {
                        throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in parsing the UPDATE packet - " + e.getMessage());
                    }

                    double totalTrust = bgpPacket.getTotalTrust();

                    this.updateRoutingTableTrusts(sourceIpAddress, destinationIpAddress, totalTrust);
                }

                // Receiving OPEN packet
                else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(5) == '1') {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] OPEN packet sucessfully received on interface " + interfaceName);

                    // TODO If needed, get router ID, AS from this object
                    BgpPacket bgpPacket2 = new BgpPacket(tcpPacket2.getData());

                    // Change BGP state to OpenConfirm
                    destInt.setState(BGPStates.OpenConfirm);
                    System.out.println("\033[0;35m" + "[" + dest.getName() + " - " + destInt.getName() + "] BGP state : OpenConfirm" + "\033[0m");
                }

                // Receiving UPDATE packet
                else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(6) == '1') {
                    List<Boolean> isUpdated;
                    // check dest router state, if =/= Established, drop packet
                    if (destInt != null && destInt.getState() != BGPStates.Established) {
                        throw new Exception("[" + srcRouterName + ":" + srcInt.getName() + " -> " + destRouterName + ":" + destInt.getName()
                                + "] Destination router is not in Established state. Packet dropped!"
                                + " Destination router state: " + destInt.getState());
                    }

                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] UPDATE packet sucessfully received on interface " + interfaceName);

                    UpdateMessagePacket bgpPacket2 = null;
                    try {
                        String stringedPkt = tcpPacket2.getData().substring(8); // remove the header of first 8 bits
                        bgpPacket2 = new UpdateMessagePacket(stringedPkt);
                    } catch (Exception e) {
                        throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in parsing the UPDATE packet - " + e.getMessage());
                    }

                    try {
                        //insert the entry in the table
                        // output [removedRoutes, addedRoutes]
                        isUpdated = updateTable(srcRouterName, destRouterName, bgpPacket2);

                        System.out.println("\033[0;35m" + "[" + dest.getName() + " - " + destInt.getName() + "] BGP tables [hasDeletedRoutes, BGPTableUpdated] : " + isUpdated + "\033[0m");

                        //Send UPDATE packet to all neighbors
                        if (isUpdated.get(0) || isUpdated.get(1)) {
                            // get router's neighbors
                            UpdateMessagePacket finalBgpPacket = bgpPacket2;
                            for (NeighborTableEntry entry : dest.getNeighborTable().getNeighborInfo()) {
                                String ip = entry.getIp();
                                // avoids to send update to the router that sent the update in the first place
                                if (!sourceIpAddress.equals(ip)) {
                                    // send an update to neighbor for each change
                                    System.out.println("[" + dest.getName() + " -> " + ip +
                                            "] Forwarding UPDATE (Withdrawn routes + New routes) packet to neighbor " +
                                            ip + " @ " + getRouterByIP(ip).getName());

                                    // adding path segment to tell that the update went through this router
                                    String[] segmentVal = new String[1];
                                    segmentVal[0] = destinationIpAddress;
                                    PathSegments pathSegments = new PathSegments(destinationIpAddress, segmentVal);
                                    finalBgpPacket.getPathAttributes().addAsPathSegment(pathSegments);

                                    // adding next hop to tell
                                    finalBgpPacket.getPathAttributes().setNEXT_HOP(sourceIpAddress);

                                    // send update packet
                                    SendUpdateMessage task = new SendUpdateMessage(destinationIpAddress, ip,
                                            finalBgpPacket.getWithdrawnRoutes(),
                                            finalBgpPacket.getPathAttributes(),
                                            finalBgpPacket.getNetworkLayerReachabilityInformation());
                                    ThreadPool.submit(task);
                                }
                            }
                        } else {
                            // NO FORWARDING - Print routing table & topology table

//                            dest.printRoutingTable();
//                            dest.printTopologyTable();

                            System.out.println("[" + dest.getName() + " - " + destInt.getName() + "] Update Packet has not been forwarded to neighbors");
                        }
                    } catch (Exception e) {
                        throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in handling tables - " + e.getMessage());
                    }

                }


                // Receiving SYN + ACK packet
                else if (tcpPacket2.isSyn() && tcpPacket2.isAck()) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] SYN + ACK packet sucessfully received on interface " + interfaceName);
                }

                // Receiving SYN packet
                else if (tcpPacket2.isSyn()) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] SYN packet sucessfully received on interface " + interfaceName);
                }

                // Receiving RST packet
                else if (tcpPacket2.isRst()) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] RST packet sucessfully received on interface " + interfaceName);
                    try {
                        establishTcpConnection(destinationIpAddress, sourceIpAddress);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }

                // Receiving ACK packet
                else if (tcpPacket2.isAck()) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] ACK packet sucessfully received on interface " + interfaceName);

                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] TCP connection established!");
                }
            }
        }
    }

    public boolean updateRoutingTableTrusts(String srcIp, String destIp, double trust) {

        Router r = Router.getRouterByIP(srcIp);
        assert r != null;
        TopologyTable topologyTable = r.getTopologyTable();

        System.out.println("################################ " + r.getName() + " " + destIp + " " + trust);

//        for (Map.Entry<String, PathAttributes> entry : topologyTable.getTopTable().entrySet()) {
//            if(entry.getKey().equals(destIp)) {
//                PathAttributes attributes = entry.getValue();
//                attributes.setTRUSTRATE(trust);
////                topologyTable.addTrustToEntryByIp(entry.getKey(), attributes);
//            }
//        }

//        r.setTopologyTable(topologyTable);

        //update the BGP routing table
        //if something changed, return true
//        boolean addedRoutes = r.updateBGPRoutingTable();

        r.printRoutingTable();

        return false;
    }

    //takes the update message and insert the value in the table, return a boolean if the entry if something has changed
    // output boolean: [0] = true if routes has been removed, [1] = true if the route has been added
    public List<Boolean> updateTable(String srcRouterName, String destRouterName, BgpPacket bgpPacket) throws Exception {
        try {
            //get the router by the name
            boolean addedRoutes;
            boolean removedRoutes = false;
            if (Globals.routerNames.contains(destRouterName)) {

                Router r = Router.getRouterByName(destRouterName);
                assert r != null;
                TopologyTable topologyTable = r.getTopologyTable();
                List<Map<Integer, String>> withdrawnRoutes = ((UpdateMessagePacket) bgpPacket).getWithdrawnRoutes(); //<length, IP_prefix>
                List<Map<Integer, String>> networkLayerReachabilityInformation = ((UpdateMessagePacket) bgpPacket).getNetworkLayerReachabilityInformation(); //<length, IP_prefix>
                PathAttributes pathAttributes = ((UpdateMessagePacket) bgpPacket).getPathAttributes();

                try {
                    // Delete the withdrawn routes
                    for (Map<Integer, String> entry : withdrawnRoutes) {
                        for (Map.Entry<Integer, String> entry2 : entry.entrySet()) {

                            String prefix = entry2.getValue();

                            if (!removedRoutes) {
                                removedRoutes = topologyTable.removeEntryByIp(prefix);
                            } else {
                                topologyTable.removeEntryByIp(prefix);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in removing the withdrawn routes - " + e.getMessage());
                }

                try {
                    //insert into the topology table the entry <String origin, <String destinationIp, String[] pathSegmentValue>, String nextHop>
                    //put PathAttribute and NLRI together
                    for (Map<Integer, String> entry : networkLayerReachabilityInformation) {
                        for (Map.Entry<Integer, String> entry2 : entry.entrySet()) {
                            topologyTable.insertEntry(entry2.getValue(), pathAttributes);
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in inserting the new routes in TopologyTable - " + e.getMessage());
                }

                //update the topology table
                r.setTopologyTable(topologyTable);
//                r.printTopologyTable();

                //update the BGP routing table
                //if something changed, return true
                addedRoutes = r.updateBGPRoutingTable();
                if (addedRoutes || removedRoutes) {
                    System.out.println("[" + destRouterName + "] Routing table updated!");
                }

                r.printRoutingTable();
            } else {
                throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] Router " + destRouterName + " not found!");
            }
            List<Boolean> output = new ArrayList<>();
            output.add(removedRoutes);
            output.add(addedRoutes);
            return output;
        } catch (Exception o) {
            throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] " + "Error in updating the BGP table - " + o.getMessage());
        }
    }

    @Override
    public void run() {

        try {
            this.receiveTcpPacket(bitArrayHdlc);
        } catch (Exception e) {
            System.err.println("Error in Receiving TCP packet - " + e.getMessage());
        }
    }

}
