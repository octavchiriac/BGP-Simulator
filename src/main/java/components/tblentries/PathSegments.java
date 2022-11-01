package components.tblentries;

import java.util.ArrayList;

public class PathSegments {
    private String destinationIp;
    private String[] pathSegmentValue; // List of all possible paths for destinationIp

    public PathSegments(String destinationIp, String[] pathSegmentValue){
        this.destinationIp = destinationIp;
        this.pathSegmentValue = pathSegmentValue;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public String[] getPathSegmentValue() {
        return pathSegmentValue;
    }


}
