package multithread;

import components.Globals;
import components.Router;
import packets.TrustListMessagePacket;

import java.util.Map;

import static components.Globals.BGP_VERSION;

public class SendTrustListMessage implements Runnable {

    private String source;
    private String destination;
    private Map<Integer, String> trustList;

    public SendTrustListMessage(String source, String destination, Map<Integer, String> trustList) {
        this.source = source;
        this.destination = destination;
        this.trustList = trustList;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        assert r1 != null;

        Router r2 = Router.getRouterByIP(this.destination);
        assert r2 != null;

        try {
            while (r1.getTcpConnectedRouters().contains(r2)) {
                TrustListMessagePacket packet1 = new TrustListMessagePacket(BGP_VERSION, 0, 0, 0, this.trustList);
                String bitArrayBgp1 = packet1.packetToBitArray();

                SendTcpPacket task1 = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 1, 1,
                        this.source, this.destination, Globals.DESTINATION_MAC_ADDRESS,
                        false, true, true, false, bitArrayBgp1);
                ThreadPool.submit(task1);

                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
