package packets;

import utils.BinaryFunctions;

public class TrustMessagePacket extends BgpPacket {

    private double totalTrust;

    public TrustMessagePacket(int version, int as, int holdTime, long id, double totalTrust) {

        super(version, as, holdTime, id);
        this.totalTrust = totalTrust;
    }

    public TrustMessagePacket(String bitsArray) {
        super(bitsArray); // version, as, holdTime, id --> HEADER
        String stringedTrust = null;

        try {
            stringedTrust = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 56,
                    64, String.class);
        } catch (Exception e) {
            System.out.println("Error in Deserializing TrustMessagePacket: " + e.getMessage());
        }

        stringedTrust = stringedTrust.replaceAll("/,/", "\\."); // Replace : with .

        this.totalTrust = Double.parseDouble(stringedTrust);
    }

    public String packetToBitArray() {
        //   1 - OPEN - 00000001
        //   2 - UPDATE - 00000010
        //   3 - NOTIFICATION - 00000011
        //   4 - KEEPALIVE - 00000100
        //   5 - TRUSTRATE - 00000101
        String headerInBits = "00000101"; // 8 bits

        String stringedTotalTrust = Double.toString(totalTrust).replaceAll("\\.", "/,/"); // Replace all dots with /:/ due to the fact that dots are used as separators in toBitsArray

        String bitsArray = headerInBits + super.packetToBitArray();

        bitsArray += BinaryFunctions.toBitsArray(stringedTotalTrust, stringedTotalTrust.length());

        return bitsArray;
    }

    public double getTotalTrust() {
        return totalTrust;
    }

    @Override
    public String toString() {
        return "TrustMessagePacket{" +
                super.toString() +
                ", TotalTrust=" + totalTrust +
                '}';
    }
}