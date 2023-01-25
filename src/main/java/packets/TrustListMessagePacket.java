package packets;

import utils.BinaryFunctions;
import utils.ParserList;

import java.util.Map;

public class TrustListMessagePacket extends BgpPacket {

    private Map<Integer, String> trustList;
    private long trustListLength;

    public TrustListMessagePacket(int version, int as, int holdTime, long id, Map<Integer, String> trustList) {

        super(version, as, holdTime, id);
        this.trustList = trustList;
    }

    public TrustListMessagePacket(String bitsArray) {
        super(bitsArray); // version, as, holdTime, id --> HEADER
        try {
            this.trustListLength = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 56, 16, Long.class);

            String stringedParsedMap = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 72,
                    (int) this.trustListLength, String.class);
            String parsedMap = stringedParsedMap.replaceAll("/:/", "\\."); // Replace : with .
            this.trustList = ParserList.StringToMap(parsedMap);
        } catch (Exception e) {
            System.out.println("Error in Deserializing TrustListMessagePacket: " + e.getMessage());
        }
    }

    public String packetToBitArray() {
        //   1 - OPEN - 00000001
        //   2 - UPDATE - 00000010
        //   3 - NOTIFICATION - 00000011
        //   4 - KEEPALIVE - 00000100
        //   5 - TRUSTRATE - 00000101
        //   6 - TRUSTLIST - 00000110
        String headerInBits = "00000110"; // 8 bits

        String parsedMap = ParserList.MapToString(this.trustList);
        String stringedParsedMap = parsedMap.replaceAll("\\.", "/:/"); // Replace all dots with /:/ due to the fact that dots are used as separators in toBitsArray

        this.trustListLength = BinaryFunctions.toBitsArray(stringedParsedMap, stringedParsedMap.length()).length();

        String bitsArray = headerInBits + super.packetToBitArray() +
                BinaryFunctions.toBitsArray(this.trustListLength, 16);

        bitsArray += BinaryFunctions.toBitsArray(stringedParsedMap, stringedParsedMap.length());

        return bitsArray;
    }

    public Map<Integer, String> getTrustList() {
        return trustList;
    }

    @Override
    public String toString() {
        return "TrustMessagePacket{" +
                super.toString() +
                ", TrustList=" + trustList +
                '}';
    }
}