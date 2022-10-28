package components;

import java.util.ArrayList;

public class TopologyTableEntry {
    public String destinationIp;
    public String nextHop;
    public String metric;
    public ArrayList<String> pathAS;

    public TopologyTableEntry(String destinationIp, String nextHop, String metric, ArrayList<String> pathAS){
        this.destinationIp = destinationIp;
        this.nextHop = nextHop;
        this.metric = metric;
        this.pathAS = pathAS;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public String getNextHop() {
        return nextHop;
    }

    public String getMetric() {
        return metric;
    }

    public ArrayList<String> getPathAS() {
        return pathAS;
    }
}
