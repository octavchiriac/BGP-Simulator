package packets;

import components.tblentries.PathAttributes;
import multithread.SendTcpPacket;
import utils.BinaryFunctions;
import utils.ParserList;

import java.util.HashMap;
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
public class UpdateMessagePacket extends BgpPacket {
    private long WithdrawnRoutesLength;
    private List<Map<Integer, String>> WithdrawnRoutes; // Represented as <length, IP_prefix>
    private long TotalPathAttributeLength;
    private PathAttributes PathAttributes;
    private List<Map<Integer, String>> NetworkLayerReachabilityInformation; // Represented as <length, IP_prefix>

    public UpdateMessagePacket(int version, int as, int holdTime, long id, long WithdrawnRoutesLength, List<Map<Integer, String>> WithdrawnRoutes, long TotalPathAttributeLength,
                               PathAttributes PathAttributes, List<Map<Integer, String>> NetworkLayerReachabilityInformation) {

        super(version, as, holdTime, id);
        this.WithdrawnRoutesLength = WithdrawnRoutesLength;
        this.WithdrawnRoutes = WithdrawnRoutes;
        this.TotalPathAttributeLength = TotalPathAttributeLength;
        this.PathAttributes = PathAttributes;
        this.NetworkLayerReachabilityInformation = NetworkLayerReachabilityInformation;
    }

    public void dispatch(int sourcePort, int destinationPort, int seqNumber, int ackNumber,
                         String sourceIpAddress, String destinationIpAddress, String destinationMacAddress) {
        SendTcpPacket sendTcpPacket = new SendTcpPacket(sourcePort, destinationPort, seqNumber, ackNumber,
                sourceIpAddress, destinationIpAddress, destinationMacAddress, false, true, false, false, this.toString());

    }

    public UpdateMessagePacket(String bitsArray) {
        super(bitsArray);
        this.WithdrawnRoutesLength = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 16, Long.class);
        this.TotalPathAttributeLength = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 16, 16, Long.class);
        this.PathAttributes = new PathAttributes(bitsArray.substring(24, (int) this.TotalPathAttributeLength));

        this.WithdrawnRoutes = ParserList.parseString((String) BinaryFunctions.bitsArrayToObject(bitsArray, 24 + (int) this.TotalPathAttributeLength, (int) this.WithdrawnRoutesLength, String.class));
        this.NetworkLayerReachabilityInformation = ParserList.parseString((String) BinaryFunctions.bitsArrayToObject(bitsArray, 24 + (int) this.TotalPathAttributeLength + (int) this.WithdrawnRoutesLength, bitsArray.length() - 24 - (int) this.TotalPathAttributeLength - (int) this.WithdrawnRoutesLength, String.class));
    }

    public String packetToBitArray(){
        String bitsArray = super.packetToBitArray() +
                BinaryFunctions.toBitsArray(this.WithdrawnRoutesLength, 16) +
                BinaryFunctions.toBitsArray(this.TotalPathAttributeLength, 16) +
                this.PathAttributes.packetToBitArray();

        String stringedWithdrawnRoutes = ParserList.parseList(this.WithdrawnRoutes);
        bitsArray += BinaryFunctions.toBitsArray(stringedWithdrawnRoutes, stringedWithdrawnRoutes.length());

        String stringedNetworkLayerReachabilityInformation = ParserList.parseList(this.NetworkLayerReachabilityInformation);
        bitsArray += BinaryFunctions.toBitsArray(stringedNetworkLayerReachabilityInformation, stringedNetworkLayerReachabilityInformation.length());

        return bitsArray;
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

    public PathAttributes getPathAttributes() {
        return PathAttributes;
    }

    public List<Map<Integer, String>> getNetworkLayerReachabilityInformation() {
        return NetworkLayerReachabilityInformation;
    }


}