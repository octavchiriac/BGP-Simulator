package components.tblentries;

import utils.BinaryFunctions;

import java.net.Inet4Address;
import java.util.Arrays;

public class PathSegments {
    private String destinationIp;
    private String[] pathSegmentValue; // List of all possible AS the update packet has passed through

    public PathSegments(String destinationIp, String[] pathSegmentValue) {
        this.destinationIp = destinationIp;
        this.pathSegmentValue = pathSegmentValue;
    }

    @Override
    public String toString() {
        return "pathSegmentValue=" + Arrays.toString(pathSegmentValue);
    }

    public PathSegments(String bitsArray) {

        String[] input = bitsArray.split("/");
        this.destinationIp = (String) BinaryFunctions.bitsArrayToObject(input[0], 0, 32, Inet4Address.class);

        String[] pathSegmentVal = input[1].split(";");

        int i = 0;
        this.pathSegmentValue = new String[pathSegmentVal.length];
        for (String ps : pathSegmentVal) {
            this.pathSegmentValue[i] = ((String) BinaryFunctions.bitsArrayToObject(ps, 0, ps.length(), Inet4Address.class));
            i++;
        }
    }

    public String toBitArrayString() {

        String bitArrayString = "";
        bitArrayString += BinaryFunctions.toBitsArray(destinationIp, 32) + "/";

        for (String pathSegment : pathSegmentValue) {
            bitArrayString += BinaryFunctions.toBitsArray(pathSegment, 32) + ";";
        }

        return bitArrayString;
    }

    public String[] getPathSegmentValue() {
        return pathSegmentValue;
    }
}
