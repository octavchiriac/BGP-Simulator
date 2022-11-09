package packets;

import components.tblentries.PathAttributes;
import multithread.SendTcpPacket;
import utils.BinaryFunctions;
import utils.ParserList;

import java.awt.*;
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

    public UpdateMessagePacket(int version, int as, int holdTime, long id, List<Map<Integer, String>> WithdrawnRoutes,
                               PathAttributes PathAttributes, List<Map<Integer, String>> NetworkLayerReachabilityInformation) {

        super(version, as, holdTime, id);
        this.WithdrawnRoutes = WithdrawnRoutes;
        this.PathAttributes = PathAttributes;
        this.NetworkLayerReachabilityInformation = NetworkLayerReachabilityInformation;
    }

    public UpdateMessagePacket(String bitsArray) {
        super(bitsArray);

        System.out.println(super.toString());
        System.out.println(super.packetToBitArray());
        System.out.println(bitsArray.substring(0, 56));

        System.out.println("bitsArray arrived");
        this.WithdrawnRoutesLength = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 56, 16, Long.class);
        System.out.println("WithdrawnRoutesLength: " + this.WithdrawnRoutesLength);
        this.TotalPathAttributeLength = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 72, 16, Long.class);
        System.out.println("TotalPathAttributeLength: " + this.TotalPathAttributeLength);
        System.out.println("Total pkt length: " + bitsArray.length());

        System.out.println("PathAttributeLength in bits RECEIVED " + bitsArray.substring(72, 72 + 16));

        System.out.println(bitsArray.substring(88, (int) this.TotalPathAttributeLength));

        this.PathAttributes = new PathAttributes(bitsArray.substring(88, (int) this.TotalPathAttributeLength));
        System.out.println(this.PathAttributes.toString());


        // TODO: Fix this
        this.WithdrawnRoutes = ParserList.parseString((String) BinaryFunctions.bitsArrayToObject(bitsArray, 88 + (int) this.TotalPathAttributeLength, (int) this.WithdrawnRoutesLength, String.class));
        this.NetworkLayerReachabilityInformation = ParserList.parseString((String) BinaryFunctions.bitsArrayToObject(bitsArray, 88 + (int) this.TotalPathAttributeLength + (int) this.WithdrawnRoutesLength, bitsArray.length() - 24 - (int) this.TotalPathAttributeLength - (int) this.WithdrawnRoutesLength, String.class));
    }

    public String packetToBitArray() {
        System.out.println("UpdateMessagePacket.packetToBitArray");

        //   1 - OPEN - 00000001
        //   2 - UPDATE - 00000010
        //   3 - NOTIFICATION - 00000011
        //   4 - KEEPALIVE - 00000100
        String headerInBits = "00000010"; // 8 bits

        String parsedWithdrawnRoutes = ParserList.parseList(this.WithdrawnRoutes);
        String stringedWithdrawnRoutes = parsedWithdrawnRoutes.replaceAll("\\.", "/:/"); // Replace all dots with /:/ due to the fact that dots are used as separators in toBitsArray

        String parsedNetworkLayerReachabilityInformation = ParserList.parseList(this.NetworkLayerReachabilityInformation);
        String stringedNetworkLayerReachabilityInformation = parsedNetworkLayerReachabilityInformation.replaceAll("\\.", "/:/"); // Replace all dots with /:/

        String pathAttributesInBits = this.PathAttributes.packetToBitArray();

        this.WithdrawnRoutesLength = stringedWithdrawnRoutes.length();
        this.TotalPathAttributeLength = pathAttributesInBits.length();

        System.out.println("PacketHeader " + super.toString());
        System.out.println("PacketHeader in bits " + super.packetToBitArray());

        System.out.println("WithdrawnRoutesLength in BITS " + BinaryFunctions.toBitsArray(this.WithdrawnRoutesLength, 16));
        System.out.println("TotalPathAttributeLength in BITS " + BinaryFunctions.toBitsArray(this.TotalPathAttributeLength, 16));
        System.out.println("PathAttributeLength in BITS " + pathAttributesInBits);
        System.out.println("WithdrawnRoutesLength in BITS " + BinaryFunctions.toBitsArray(this.WithdrawnRoutesLength, 16));
        System.out.println("STRINGED WithdrawnRoutes in BITS " + stringedWithdrawnRoutes);
        System.out.println("WithdrawnRoutes in BITS " + BinaryFunctions.toBitsArray(stringedWithdrawnRoutes, stringedWithdrawnRoutes.length()));
        System.out.println("STRINGED NetworkLayerReachabilityInformation in BITS " + stringedNetworkLayerReachabilityInformation);
        System.out.println("NetworkLayerREchabilityInformation in BITS " + BinaryFunctions.toBitsArray(stringedNetworkLayerReachabilityInformation, stringedNetworkLayerReachabilityInformation.length()));


        String bitsArray = headerInBits + super.packetToBitArray() +
                BinaryFunctions.toBitsArray(this.WithdrawnRoutesLength, 16) +
                BinaryFunctions.toBitsArray(this.TotalPathAttributeLength, 16) +
                pathAttributesInBits;

        bitsArray += BinaryFunctions.toBitsArray(stringedWithdrawnRoutes, stringedWithdrawnRoutes.length());

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

    @Override
    public String toString() {
        return "UpdateMessagePacket{" +
                "WithdrawnRoutesLength=" + WithdrawnRoutesLength +
                ", WithdrawnRoutes=" + WithdrawnRoutes +
                ", TotalPathAttributeLength=" + TotalPathAttributeLength +
                ", PathAttributes=" + PathAttributes +
                ", NetworkLayerReachabilityInformation=" + NetworkLayerReachabilityInformation +
                ", data='" + data +
                '}';
    }
}