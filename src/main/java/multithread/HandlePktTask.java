package multithread;

import components.TCPpacket;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;
import utils.TypeHandlers;

public class HandlePktTask implements Runnable {

    private String bitStream;
    private String master;

    public HandlePktTask(String bs, String master) {
        this.bitStream = bs;
        this.master = master;
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
        TcpPacket tcpLayer = new TcpPacket(ipLayer.getData());

        // Handling packet
        // TODO implement packet handling
        System.out.println("Packet received at router " + master + " at " + TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
        System.out.println("\n");
        System.out.println(tcpLayer.toString());
        System.out.println("\n");
    }
}
