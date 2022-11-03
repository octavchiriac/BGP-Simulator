package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import components.tblentries.PathAttributes;
import packets.BgpPacket;
import packets.UpdateMessagePacket;

import java.util.List;
import java.util.Map;

import static components.Globals.BGP_VERSION;
import static components.Globals.HOLD_TIMER;

/*
    Major fields of the BGP update message are as follows:

        - Unfeasible routes length—The total length of the withdrawn routes field in bytes.
            A value of 0 indicates no route is withdrawn from service, nor is the withdrawn routes field present in this update message.
        - Withdrawn routes—This is a variable length field that contains a list of withdrawn IP prefixes.
        - Total path attribute length—Total length of the path attributes field in bytes.
            A value of 0 indicates that no NLRI field is present in this update message.
        - Path attributes—List of path attributes related to NLRI.
            Each path attribute is a triple <attribute type, attribute length, attribute value> of variable length.
            BGP uses these attributes to avoid routing loops, and perform routing and protocol extensions.
        - NLRI—Each feasible route is represented as <length, prefix>.
 */

public class SendUpdateMessage implements Runnable {

    private String source;
    private String destination;
    private long WithdrawnRoutesLength;
    private List<Map<Integer, String>> WithdrawnRoutes; // Represented as <length, IP_prefix>
    private long TotalPathAttributeLength;
    private components.tblentries.PathAttributes PathAttributes;
    private List<Map<Integer, String>> NetworkLayerReachabilityInformation; // Represented as <length, IP_prefix>

    public SendUpdateMessage(String source, String destination, long withdrawnRoutesLength, List<Map<Integer, String>> withdrawnRoutes,
                             long totalPathAttributeLength, components.tblentries.PathAttributes pathAttributes,
                             List<Map<Integer, String>> networkLayerReachabilityInformation) {
        this.source = source;
        this.destination = destination;
        WithdrawnRoutesLength = withdrawnRoutesLength;
        WithdrawnRoutes = withdrawnRoutes;
        TotalPathAttributeLength = totalPathAttributeLength;
        PathAttributes = pathAttributes;
        NetworkLayerReachabilityInformation = networkLayerReachabilityInformation;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        RouterInterface i1 = r1.getRouterInterfaceByIP(this.source);

        Router r2 = Router.getRouterByIP(this.destination);
        RouterInterface i2 = r2.getRouterInterfaceByIP(this.destination);

        BgpPacket packet1 = new UpdateMessagePacket(BGP_VERSION, Integer.parseInt(i1.getAs()), HOLD_TIMER, r1.getId(),
                this.WithdrawnRoutesLength, this.WithdrawnRoutes, this.TotalPathAttributeLength, this.PathAttributes,
                this.NetworkLayerReachabilityInformation);
        String bitArrayBgp1 = packet1.packetToBitArray();

        BgpPacket packet2 = new UpdateMessagePacket(BGP_VERSION, Integer.parseInt(i2.getAs()), HOLD_TIMER, r2.getId(), this.WithdrawnRoutesLength,
                this.WithdrawnRoutes, this.TotalPathAttributeLength, this.PathAttributes, this.NetworkLayerReachabilityInformation);
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