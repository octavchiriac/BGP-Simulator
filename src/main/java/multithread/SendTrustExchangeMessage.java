package multithread;

import components.Globals;
import components.Router;
import packets.TrustMessagePacket;

import static components.Globals.BGP_VERSION;

public class SendTrustExchangeMessage implements Runnable {

    private String source;
    private String destination;
    private double totalTrust;

    public SendTrustExchangeMessage(String source, String destination, double totalTrust) {
        this.source = source;
        this.destination = destination;
        this.totalTrust = totalTrust;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        assert r1 != null;

        Router r2 = Router.getRouterByIP(this.destination);
        assert r2 != null;

        TrustMessagePacket packet1 = new TrustMessagePacket(BGP_VERSION, 0,0,0, this.totalTrust);
        String bitArrayBgp1 = packet1.packetToBitArray();

        if (r1.getTcpConnectedRouters().contains(r2)) {
            SendTcpPacket task1 = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 1, 1,
                    this.source, this.destination, Globals.DESTINATION_MAC_ADDRESS,
                    false, true, true, false, bitArrayBgp1);
            ThreadPool.submit(task1);
        }
    }
}
