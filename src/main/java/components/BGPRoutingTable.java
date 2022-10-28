package components;

import java.util.ArrayList;

public class BGPRoutingTable {
/*
 * "BGP Routing Table – table containing information about the best path to each destination network"
 * BGP Routing Table – the main IP routing tables that contains only the best routes from BGP Table.
 * After BGP has selected the best path to a network, that path is added to the main IP routing table.
 *
 * https://support.huawei.com/enterprise/en/doc/EDOC1000178110/81bd490c/bgp-routing-table
 */


    public String localRouterId; //Router ID of the local device, in the same format as an IPv4 address.
    public int localASNumber; //Local AS number.
    public String paths; //BGP route information.
    public int routeDuration; //Duration of a route.
    public String advertisedRouterId; //IP address of the device that advertised the route.
    public String nextHop; //Next hop IP address.
    public String outInterface; //Outbound interface of the route to which the BGP route is iterated.
    public String pathAS;

    public BGPRoutingTable() {
        super();

    }
    public void setFromNeighbour(NeighborTable neighborTable) {
        ArrayList<String> neighborsIp = neighborTable.getNeighborsIp();
        ArrayList<String> neighborsAs = neighborTable.getNeighborsAs();

        for(int i = 0; i < neighborsIp.size(); i++) {
            //TODO: fill the values based on the neighborTable, search for the ip address based on the neighbour
        }
    }
    public void setBGPRoutingTable(String localRouterId, int localASNumber, String paths, int routeDuration, String advertisedRouterId, String nextHop, String outInterface, String pathAS){
        this.localRouterId = localRouterId;
        this.localASNumber = localASNumber;
        this.paths = paths;
        this.routeDuration = routeDuration;
        this.advertisedRouterId = advertisedRouterId;
        this.nextHop = nextHop;
        this.outInterface = outInterface;
        this.pathAS = pathAS;
    }
    public int getLocalASNumber() {
        return localASNumber;
    }

    public void setLocalASNumber(int localASNumber) {
        this.localASNumber = localASNumber;
    }

    public String getLocalRouterId() {
        return localRouterId;
    }

    public void setLocalRouterId(String localRouterId) {
        this.localRouterId = localRouterId;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public int getRouteDuration() {
        return routeDuration;
    }

    public void setRouteDuration(int routeDuration) {
        this.routeDuration = routeDuration;
    }

    public String getAdvertisedRouterId() {
        return advertisedRouterId;
    }

    public void setAdvertisedRouterId(String advertisedRouterId) {
        this.advertisedRouterId = advertisedRouterId;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getOutInterface() {
        return outInterface;
    }

    public void setOutInterface(String outInterface) {
        this.outInterface = outInterface;
    }

    public String getPathAS() {
        return pathAS;
    }

    public void setPathAS(String pathAS) {
        this.pathAS = pathAS;
    }


    @Override
    public String toString() {
        return "RoutingTable{" +
                "localRouterId='" + localRouterId + '\'' +
                ", localASNumber='" + localASNumber + '\'' +
                ", paths='" + paths + '\'' +
                ", routeDuration='" + routeDuration + '\'' +
                ", advertisedRouterId='" + advertisedRouterId + '\'' +
                ", nextHop='" + nextHop + '\'' +
                ", outInterface='" + outInterface + '\'' +
                ", pathAS='" + pathAS + '\'' +
                '}';
    }
}
