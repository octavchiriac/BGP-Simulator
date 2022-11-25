package components;

import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;

import java.util.ArrayList;
import java.util.HashMap;

public class BGPRoutingTable {
/*
 * "BGP Routing Table – table containing information about the best path to each destination network"
 * BGP Routing Table – the main IP routing tables that contains only the best routes from BGP Table.
 * After BGP has selected the best path to a network, that path is added to the main IP routing table.
 *
 * https://support.huawei.com/enterprise/en/doc/EDOC1000178110/81bd490c/bgp-routing-table
 */

    // the new BGP routing table will have the pairs (DestinationIP, BestPath)
    public HashMap<String, String[]> bestRoutes;

    public BGPRoutingTable() {
        super();
        bestRoutes = new HashMap<String, String[]>();
    }

    /*
        * This method is called when there is the need of updating the BGP Routing Table with the best paths from the RIB
        * The best path can be changed also based on the policies (like shortest path, lowest cost, etc)
     */
    public boolean updateTable(TopologyTable topologyTable) {
        ArrayList<PathAttributes> listRIB = topologyTable.getListRIB();
        boolean changed= false;

        System.out.println(this.toString());

        for (PathAttributes entry : listRIB) {
            String tmpNextHop = entry.getNEXT_HOP();
            PathSegments[] tmpPath= entry.getAS_PATH();
            for (PathSegments pathSegment : tmpPath) {
                String tmpDestinationIp = pathSegment.getDestinationIp();
                //list of IPs
                String[] tmpPathSegmentValue = pathSegment.getPathSegmentValue();
                //if the destination ip is already inside, check if the bestpath is good
                if (bestRoutes.get(tmpDestinationIp) != null) {
                    //if the best path is shorter (in terms of array size) than the one already in, update the best path
                    if (bestRoutes.get(tmpDestinationIp).length > tmpPathSegmentValue.length) {
                        bestRoutes.put(tmpDestinationIp, tmpPathSegmentValue);
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    public HashMap<String, String[]> getBestRoutes() {
        return bestRoutes;
    }

    @Override
    public String toString() {
        String result = "";
        for (String key : bestRoutes.keySet()) {
            result += "|Destination IP: " + key + " | Best Path: " + bestRoutes.get(key) + ";";
        }
        return result;
    }
}
