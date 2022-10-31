package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.BgpPacket;

import static components.Globals.BGP_VERSION;
import static components.Globals.HOLD_TIMER;

public class SendOpenMessage implements Runnable{

    String source;
    String destination;

    public SendOpenMessage(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        RouterInterface i1 = r1.getRouterInterfaceByIP(this.source);

        Router r2 = Router.getRouterByIP(this.destination);
        RouterInterface i2 = r2.getRouterInterfaceByIP(this.destination);

        BgpPacket packet1 = new BgpPacket(BGP_VERSION, Integer.parseInt(i1.getAs()), HOLD_TIMER, r1.getId());
        String bitArrayBgp1 = packet1.packetToBitArray();

        BgpPacket packet2 = new BgpPacket(BGP_VERSION, Integer.parseInt(i2.getAs()), HOLD_TIMER, r2.getId());
        String bitArrayBgp2 = packet2.packetToBitArray();

        try {
                if (r1.getTcpConnectedRouters().contains(r2)) {
                    if (r1.isEnabled()) {
                        SendTcpPacket task1 = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 1, 1,
                                this.source, this.destination, Globals.DESTINATION_MAC_ADDRESS,
                                false, true, true, false, bitArrayBgp1);
                        ThreadPool.submit(task1);
                    }

                    if (r2.isEnabled()) {
                        SendTcpPacket task2 = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1,
                                this.destination, this.source, Globals.DESTINATION_MAC_ADDRESS,
                                false, true, true, false, bitArrayBgp2);
                        ThreadPool.submit(task2);
                    }

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
    }
}
