package multithread;

import components.RouterInterface;
import components.TCPpacket;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;
import utils.TypeHandlers;

import java.util.ArrayList;

public class HandlePktTask implements Runnable {

    private String bitStream;
    private String master;
    private ArrayList<RouterInterface> masterInterfaces;

    public HandlePktTask(String bs, String master, ArrayList<RouterInterface> ifaces) {
        this.bitStream = bs;
        this.master = master;
        this.masterInterfaces = ifaces;
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("Handling packet at router " + master + " - Thread " + Thread.currentThread().getId());
        // Unwrapping packet
        HdlcPacket hdlcLayer = new HdlcPacket(bitStream);
        IpPacket ipLayer = new IpPacket(hdlcLayer.getData());

        try {

            String destIP = ipLayer.getDestinationAddress();
            boolean isDestination = false;

            for (RouterInterface iface : masterInterfaces) {
                if (destIP.equals(iface.getIpAddress())) {
                    isDestination = true;
                    break;
                }
            }

            if (ipLayer.getTimeToLive() == 0) {
                throw new Exception("TTL expired");
            }

            // Forwarding packet if not at destination
            if (!isDestination) {
                // Decrementing TTL
                ipLayer.decreaseTimeToLive();
                // Re-wrapping packet
                Packet hdlcPacket = new HdlcPacket("01111110", "11111111", "00000000", ipLayer.packetToBitArray(), "00000000");
                // Re-queueing packet
                // TODO IMPLEMENT ROUTING TABLE MECHANISM TO SEND IT TO THE CORRECT NEIGHBOUR?
                SendPktTask task = new SendPktTask(hdlcLayer.packetToBitArray(), destIP);
                ThreadPool.submit(task);
            } else { // Packet arrived at destination router
                // Unwrapping TCP layer
                TcpPacket tcpLayer = new TcpPacket(ipLayer.getData());
                // Handling packet
                // TODO implement packet handling
                System.out.println("Packet received at router " + master + " at " + TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
                System.out.println("\n");
                System.out.println(ipLayer.toString());
                System.out.println("\n");
            }
        } catch (Exception e) {
            System.err.println("Packet dropped at router " + master + " - Thread " + Thread.currentThread().getId() + " - " + e.getMessage()
                    + "\n" + ipLayer.toString());
        }
    }
}
