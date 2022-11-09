package multithread;

import components.BGPStates;
import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.*;

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

                //TODO what to do here if you don't find the router in the table? do you broadcast? --> DROP THE PACKET
                ipPacket2.decreaseTimeToLive();

                Packet hdlcPacket = new HdlcPacket("01111110", Globals.DESTINATION_MAC_ADDRESS, "00000000", ipPacket2.packetToBitArray(), "00000000");

                SendTcpPacket task = new SendTcpPacket(hdlcPacket.packetToBitArray(), ipPacket2.getSourceAddress(),
                        ipPacket2.getDestinationAddress(), tcpPacket2.isSyn(), tcpPacket2.isAck(),
                        tcpPacket2.isPsh(), tcpPacket2.isRst(), tcpPacket2.getData());
                ThreadPool.submit(task);

            } else {
                /** TODO Here you need to add another if for the UPDATE packets, and somehow differentiate it from the OPEN one,
                 * Problem is that both OPEN and KEEPALIVE and UPDATE packets have the same bits on true (ACK + PSH)
                 * So maybe try to differentiate from the first bit of the data encapsulated as BGP packet(?)
                 * For example, every OPEN packet starts with the BGP version which is 4, so every packet starts like
                 * 0000010.... -> every 6th bit is 1....you can see how update packets will look like in binary
                 * and maybe try doing this to differentiate */

                // Receiving KEEPALIVE packet
                if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().length() == 0) {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] KEEPALIVE packet sucessfully received on interface " + interfaceName);

                    if (tcpPacket2.getSequenceNumber() == 0) {
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


                // Receiving OPEN packet
                else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(5) == '1') {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] OPEN packet sucessfully received on interface " + interfaceName);

                    // TODO If needed, get router ID, AS from this object
                    BgpPacket bgpPacket2 = new BgpPacket(tcpPacket2.getData());

                    // Change BGP state to OpenConfirm
                    destInt.setState(BGPStates.OpenConfirm);
                    System.out.println("\033[0;35m" + "[" + dest.getName() + " - " + destInt.getName() + "] BGP state : OpenConfirm" + "\033[0m");
                } else if (tcpPacket2.isPsh() && tcpPacket2.isAck() && tcpPacket2.getData().charAt(6) == '1') {
                    System.out.println("[" + srcRouterName + " -> " + destRouterName + "] UPDATE packet sucessfully received on interface " + interfaceName);

                    String stringedPkt = tcpPacket2.getData().substring(8); // remove the header of first 8 bits
                    System.out.println("                PACKET RECEIVED - " + tcpPacket2.getData().substring(64)); // 17 bit di troppo DIOCANE
                    BgpPacket bgpPacket2 = new UpdateMessagePacket(stringedPkt);

                    System.out.println(bgpPacket2.toString());

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

    @Override
    public void run() {

        try {
            this.receiveTcpPacket(bitArrayHdlc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
