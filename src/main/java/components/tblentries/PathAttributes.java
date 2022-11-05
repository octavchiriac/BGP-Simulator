package components.tblentries;

import utils.BinaryFunctions;
import utils.ObjectSizeFetcher;

import java.util.ArrayList;
import java.util.Arrays;

public class PathAttributes {

    private String ORIGIN;
    private PathSegments[] AS_PATH;
    private String NEXT_HOP;
    private String MULTI_EXIT_DISC = "";
    private String LOCAL_PREF = "";
    private String ATOMIC_AGGREGATE = "";
    private String AGGREGATOR = "";

    public PathAttributes(String ORIGIN, PathSegments[] AS_PATH, String NEXT_HOP) {
        this.ORIGIN = ORIGIN;
        this.AS_PATH = AS_PATH;
        this.NEXT_HOP = NEXT_HOP;
    }

    public PathAttributes(String ORIGIN, PathSegments[] AS_PATH, String NEXT_HOP, String MULTI_EXIT_DISC, String LOCAL_PREF, String ATOMIC_AGGREGATE, String AGGREGATOR) {
        this.ORIGIN = ORIGIN;
        this.AS_PATH = AS_PATH;
        this.NEXT_HOP = NEXT_HOP;
        this.MULTI_EXIT_DISC = MULTI_EXIT_DISC;
        this.LOCAL_PREF = LOCAL_PREF;
        this.ATOMIC_AGGREGATE = ATOMIC_AGGREGATE;
        this.AGGREGATOR = AGGREGATOR;
    }

    public PathAttributes(String bitsArray) {
        super();

        int asPathSize = bitsArray.length() - 48;

        this.ORIGIN = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 8, String.class);
        this.AS_PATH = (PathSegments[]) BinaryFunctions.bitsArrayToObject(bitsArray, 8, asPathSize, Arrays.class);
        this.NEXT_HOP = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 24, 8, String.class);
        this.MULTI_EXIT_DISC = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 32, 8, String.class);
        this.LOCAL_PREF = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 56, 8, String.class);
        this.ATOMIC_AGGREGATE = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 80, 8, String.class);
        this.AGGREGATOR = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 104, 8, String.class);
    }

    public String getORIGIN() {
        return ORIGIN;
    }

    public PathSegments[] getAS_PATH() {
        return AS_PATH;
    }

    public String getNEXT_HOP() {
        return NEXT_HOP;
    }

    public String getMULTI_EXIT_DISC() {
        return MULTI_EXIT_DISC;
    }

    public String getLOCAL_PREF() {
        return LOCAL_PREF;
    }

    public String getATOMIC_AGGREGATE() {
        return ATOMIC_AGGREGATE;
    }

    public String getAGGREGATOR() {
        return AGGREGATOR;
    }

    public void setMULTI_EXIT_DISC(String MULTI_EXIT_DISC) {
        this.MULTI_EXIT_DISC = MULTI_EXIT_DISC;
    }

    public void setLOCAL_PREF(String LOCAL_PREF) {
        this.LOCAL_PREF = LOCAL_PREF;
    }

    public void setATOMIC_AGGREGATE(String ATOMIC_AGGREGATE) {
        this.ATOMIC_AGGREGATE = ATOMIC_AGGREGATE;
    }

    public void setAGGREGATOR(String AGGREGATOR) {
        this.AGGREGATOR = AGGREGATOR;
    }

    public String packetToBitArray() {

        String stringedAsPathList = "";
        for (PathSegments pathSegment : AS_PATH) {
            stringedAsPathList += pathSegment.toBitArrayString() + "-";
        }
        System.out.println("stringedAsPathList: " + stringedAsPathList);

        if(this.NEXT_HOP.length() == 0){
            this.NEXT_HOP = "00000000";
        }
        if(this.MULTI_EXIT_DISC.length() == 0){
            this.MULTI_EXIT_DISC = "00000000";
        }
        if(this.LOCAL_PREF.length() == 0){
            this.LOCAL_PREF = "00000000";
        }
        if(this.ATOMIC_AGGREGATE.length() == 0){
            this.ATOMIC_AGGREGATE = "00000000";
        }
        if(this.AGGREGATOR.length() == 0){
            this.AGGREGATOR = "00000000";
        }

        String bitsArray =
                BinaryFunctions.toBitsArray(this.ORIGIN, 8) +
                        BinaryFunctions.toBitsArray(stringedAsPathList, stringedAsPathList.length()) +
                        BinaryFunctions.toBitsArray(this.NEXT_HOP, 8) +
                        BinaryFunctions.toBitsArray(this.MULTI_EXIT_DISC, 8) +
                        BinaryFunctions.toBitsArray(this.LOCAL_PREF, 8) +
                        BinaryFunctions.toBitsArray(this.ATOMIC_AGGREGATE, 8) +
                        BinaryFunctions.toBitsArray(this.AGGREGATOR, 8);
        return bitsArray;
    }
}