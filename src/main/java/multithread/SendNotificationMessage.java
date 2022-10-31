package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.BgpPacket;

import static components.Globals.*;

public class SendNotificationMessage implements Runnable{

    String source;
    String destination;

    public SendNotificationMessage(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        assert r1 != null;
        RouterInterface i1 = r1.getRouterInterfaceByIP(this.source);

        Router r2 = Router.getRouterByIP(this.destination);
        assert r2 != null;
        RouterInterface i2 = r2.getRouterInterfaceByIP(this.destination);

        BgpPacket packet1 = new BgpPacket(NOTIFICATION_DISCONNECT, 0, 0, 0);
        String bitArrayBgp1 = packet1.packetToBitArray();

        if (r1.getTcpConnectedRouters().contains(r2)) {
            SendTcpPacket task1 = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 1, 1,
                    this.source, this.destination, Globals.DESTINATION_MAC_ADDRESS,
                    false, true, true, false, bitArrayBgp1);
            ThreadPool.submit(task1);
        }
    }
}
