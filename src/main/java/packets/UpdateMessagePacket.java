package packets;

import components.tblentries.PathAttributes;
import multithread.SendTcpPacket;

import java.util.List;
import java.util.Map;

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
public class UpdateMessagePacket {
    private long WithdrawnRoutesLength;
    private List<Map<Integer, String>> WithdrawnRoutes; // Represented as <length, prefix>
    private long TotalPathAttributeLength;
    private PathAttributes PathAttributes;
    private List<Map<Integer, String>> NetworkLayerReachabilityInformation; // Represented as <length, prefix>

    public UpdateMessagePacket(long WithdrawnRoutesLength, List<Map<Integer, String>> WithdrawnRoutes, long TotalPathAttributeLength,
                         PathAttributes PathAttributes, List<Map<Integer, String>> NetworkLayerReachabilityInformation) {
        this.WithdrawnRoutesLength = WithdrawnRoutesLength;
        this.WithdrawnRoutes = WithdrawnRoutes;
        this.TotalPathAttributeLength = TotalPathAttributeLength;
        this.PathAttributes = PathAttributes;
        this.NetworkLayerReachabilityInformation = NetworkLayerReachabilityInformation;
    }

    public void dispatch(int sourcePort, int destinationPort, int seqNumber, int ackNumber,
                         String sourceIpAddress, String destinationIpAddress, String destinationMacAddress){
        SendTcpPacket sendTcpPacket = new SendTcpPacket(sourcePort, destinationPort, seqNumber, ackNumber,
                sourceIpAddress, destinationIpAddress, destinationMacAddress, false, true, false, false, this.toString());

    }

    public long getWithdrawnRoutesLength() {
        return WithdrawnRoutesLength;
    }

    public List<Map<Integer, String>> getWithdrawnRoutes() {
        return WithdrawnRoutes;
    }

    public long getTotalPathAttributeLength() {
        return TotalPathAttributeLength;
    }

    public components.tblentries.PathAttributes getPathAttributes() {
        return PathAttributes;
    }

    public List<Map<Integer, String>> getNetworkLayerReachabilityInformation() {
        return NetworkLayerReachabilityInformation;
    }


}