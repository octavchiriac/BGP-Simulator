package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;

public class SendPktTask implements Runnable {
    private String destIP;
    private String srcIP;
    private String bitStream;
    private int id;

    public SendPktTask(String bs, String destIP) {
        bitStream = bs;
        this.destIP = destIP;
        this.id = Math.abs((int) System.currentTimeMillis() + (int) (Math.random() * 100));
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
        Router dest = getRouterByIP(destIP); // TODO IMPLEMENT ROUTING NEIGHBOURS HERE?
        if (dest != null) {
            // queueing packet at destination router
            dest.queuePacket(bitStream, id);
        } else {
            System.err.println("Destination router " + destIP + " not found, packet dropped at Link Level " + id);
        }
    }
}
