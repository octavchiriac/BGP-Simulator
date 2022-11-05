package components.tblentries;

import utils.BinaryFunctions;

import java.util.ArrayList;
import java.util.Arrays;

public class PathSegments {
    private String destinationIp;
    private String[] pathSegmentValue; // List of all possible paths for destinationIp

    public PathSegments(String destinationIp, String[] pathSegmentValue){
        this.destinationIp = destinationIp;
        this.pathSegmentValue = pathSegmentValue;
    }

    public String toBitArrayString() {

        String bitArrayString = "";
        bitArrayString += BinaryFunctions.toBitsArray(destinationIp, 32) + "/";

        for (String pathSegment : pathSegmentValue) {
            bitArrayString += BinaryFunctions.toBitsArray(pathSegment, 32) + ";";
        }

        return bitArrayString;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public String[] getPathSegmentValue() {
        return pathSegmentValue;
    }


}
