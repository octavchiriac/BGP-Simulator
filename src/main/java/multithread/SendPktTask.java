package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;

public class SendPktTask implements Runnable {
    private String destIP;
    private String destPort;
    private String srcIP;
    private String srcPort;
    private String bitStream;
    private int id;

    public SendPktTask(String destIP, String destPort, String srcIP, String srcPort) {
        this.destIP = destIP;
        this.destPort = destPort;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.id = Math.abs((int) System.currentTimeMillis());
    }

    public SendPktTask(String bs, String destIP, String destPort) {
        bitStream = bs;
        this.destIP = destIP;
        this.destPort = destPort;
        this.id = Math.abs((int) System.currentTimeMillis());
    }

    private static Router getRouterByIP(String ip) {
        for (Router r : Globals.routers) {
            for (RouterInterface i : r.getEnabledInterfaces()) {
                if (i.getIpAddress().equals(ip)) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println("Sending bitStream through link " + id);
        Router dest = getRouterByIP(destIP);
        if (dest != null) {
            // queueing packet at destination router
            dest.queuePacket(bitStream, id);
        } else {
            System.err.println("Destination router " + destIP + " not found, packet dropped at Link Level " + id);
        }
    }
}
