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
    public HashMap<String, String> bestRoutes;

    public BGPRoutingTable() {
        super();
        bestRoutes= new HashMap<String, String>();
    }

    /*
        * This method is called when there is the need of updating the BGP Routing Table with the best paths from the RIB
        * The best path can be changed also based on the policies (like shortest path, lowest cost, etc)
     */
    public void updateTable(TopologyTable topologyTable) {
        ArrayList<PathAttributes> listRIB = topologyTable.getListRIB();

        for (PathAttributes entry : listRIB) {
            String tmpNextHop = entry.getNEXT_HOP();
            PathSegments[] tmpPath= entry.getAS_PATH();
            for (PathSegments pathSegment : tmpPath) {
                String tmpDestinationIp = pathSegment.getDestinationIp();
                String[] tmpPathSegmentValue = pathSegment.getPathSegmentValue();
                String tmpBestPath = tmpPathSegmentValue[0];
                //search the shortest path for each destinationIp
                for (int i = 1; i < tmpPathSegmentValue.length; i++) {
                    // use the split with "," to get the number of AS from which the packets will pass, so we can take the shortest path
                    if (tmpPathSegmentValue[i].split(";").length < tmpBestPath.split(";").length) {
                        tmpBestPath = tmpPathSegmentValue[i];
                    }
                }
                //if the destination ip is already inside, check if the bestpath is good
                if (bestRoutes.get(tmpDestinationIp) != null) {
                    //if the best path is shorter than the one already in, update the best path
                    if (tmpBestPath.split(";").length < bestRoutes.get(tmpDestinationIp).split(";").length) {
                        bestRoutes.put(tmpDestinationIp, tmpBestPath);
                    }
                }
            }
        }


    }

    public HashMap<String, String> getBestRoutes() {
        return bestRoutes;
    }


}
