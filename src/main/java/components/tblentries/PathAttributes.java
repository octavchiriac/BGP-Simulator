package components.tblentries;

import utils.BinaryFunctions;

import java.net.Inet4Address;

public class PathAttributes {

    private String ORIGIN;
    private PathSegments[] AS_PATH;
    private String NEXT_HOP;
    private String MULTI_EXIT_DISC = "";
    private String LOCAL_PREF = "";
    private String ATOMIC_AGGREGATE = "";
    private String AGGREGATOR = "";
    private double TRUSTRATE = 0;

    public PathAttributes(String ORIGIN, PathSegments[] AS_PATH, String NEXT_HOP, double TRUSTRATE) {
        this.ORIGIN = ORIGIN; // IGP, EGP, INCOMPLETE -- 0, 1, 2 --> should be always 1
        this.AS_PATH = AS_PATH;
        this.NEXT_HOP = NEXT_HOP;
        this.TRUSTRATE = TRUSTRATE;
    }

    @Override
    public String toString() {
        StringBuilder stringedAsPath = new StringBuilder();
        for (PathSegments ps : AS_PATH) {
            stringedAsPath.append(ps.toString()).append(", ");
        }
        return "PathAttributes{" +
                "ORIGIN='" + ORIGIN + '\'' +
                ", AS_PATH=[" + stringedAsPath + "]" +
                ", NEXT_HOP='" + NEXT_HOP + '\'' +
                ", TRUST_RATE='" + TRUSTRATE + '\'' +
                ", MULTI_EXIT_DISC='" + MULTI_EXIT_DISC + '\'' +
                ", LOCAL_PREF='" + LOCAL_PREF + '\'' +
                ", ATOMIC_AGGREGATE='" + ATOMIC_AGGREGATE + '\'' +
                ", AGGREGATOR='" + AGGREGATOR + '\'' +
                '}';
    }

    public PathAttributes(String bitsArray) {
        int asPathSize = bitsArray.length() - 136;
        String stringedAsPath;

        this.ORIGIN = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 8, String.class);
        stringedAsPath = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 8, asPathSize, String.class);
        this.NEXT_HOP = (String) BinaryFunctions.bitsArrayToObject(bitsArray, asPathSize + 8, 32, Inet4Address.class);
        this.MULTI_EXIT_DISC = (String) BinaryFunctions.bitsArrayToObject(bitsArray, asPathSize + 40, 32, String.class);
        this.LOCAL_PREF = (String) BinaryFunctions.bitsArrayToObject(bitsArray, asPathSize + 72, 32, String.class);
        this.ATOMIC_AGGREGATE = (String) BinaryFunctions.bitsArrayToObject(bitsArray, asPathSize + 104, 32, String.class);
        this.AGGREGATOR = (String) BinaryFunctions.bitsArrayToObject(bitsArray, bitsArray.length() - 32, 32, String.class);

        String[] asPathSegments = stringedAsPath.split("-");
        int i = 0;
        this.AS_PATH = new PathSegments[asPathSegments.length];
        for (String o : asPathSegments) {
            this.AS_PATH[i] = new PathSegments(o);
            i++;
        }
    }

    public void addAsPathSegment(PathSegments pathSegments) {
        PathSegments[] newPathSegments = new PathSegments[AS_PATH.length + 1];
        System.arraycopy(AS_PATH, 0, newPathSegments, 0, AS_PATH.length);
        newPathSegments[AS_PATH.length] = pathSegments;
        AS_PATH = newPathSegments;
    }

    public PathSegments[] getAS_PATH() {
        return AS_PATH;
    }

    public String getNEXT_HOP() {
        return NEXT_HOP;
    }

    public void setNEXT_HOP(String NEXT_HOP) {
        this.NEXT_HOP = NEXT_HOP;
    }

    public double getTRUSTRATE() { return TRUSTRATE; }

    public String getORIGIN() {
        return ORIGIN;
    }

    public String packetToBitArray() {

        String stringedAsPathList = "";
        for (PathSegments pathSegment : AS_PATH) {
            stringedAsPathList += pathSegment.toBitArrayString() + "-";
        }

        if(this.NEXT_HOP.length() == 0) {
            this.NEXT_HOP = "000.000.000.000";
        }
        if(this.MULTI_EXIT_DISC.length() == 0 || this.MULTI_EXIT_DISC.length() > 3){
            this.MULTI_EXIT_DISC = "000";
        }
        if(this.LOCAL_PREF.length() == 0 || this.LOCAL_PREF.length() > 3){
            this.LOCAL_PREF = "000";
        }
        if(this.ATOMIC_AGGREGATE.length() == 0 || this.ATOMIC_AGGREGATE.length() > 3){
            this.ATOMIC_AGGREGATE = "000";
        }
        if(this.AGGREGATOR.length() == 0 || this.AGGREGATOR.length() > 3){
            this.AGGREGATOR = "000";
        }

        String bitsArray =
                BinaryFunctions.toBitsArray(this.ORIGIN, 8) +
                        BinaryFunctions.toBitsArray(stringedAsPathList, stringedAsPathList.length()) +
                        BinaryFunctions.toBitsArray(this.NEXT_HOP, 32) +
                        BinaryFunctions.toBitsArray(this.MULTI_EXIT_DISC, 32) +
                        BinaryFunctions.toBitsArray(this.LOCAL_PREF, 32) +
                        BinaryFunctions.toBitsArray(this.ATOMIC_AGGREGATE, 32) +
                        BinaryFunctions.toBitsArray(this.AGGREGATOR, 32);
        return bitsArray;
    }
}