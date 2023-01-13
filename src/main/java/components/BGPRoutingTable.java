package components;

import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BGPRoutingTable {
/*
 * "BGP Routing Table – table containing information about the best path to each destination network"
 * BGP Routing Table – the main IP routing tables that contains only the best routes from BGP Table.
 * After BGP has selected the best path to a network, that path is added to the main IP routing table.
 *
 * https://support.huawei.com/enterprise/en/doc/EDOC1000178110/81bd490c/bgp-routing-table
 */

    // the new BGP routing table will have the pairs (DestinationIP, BestPath)
    // public HashMap<String, String[]> bestRoutes;
    public Map<String,PathAttributes> bestRoutes ; //routing table composed by (DEST_IP,(NEXT_HOP,ORIGIN,AS_PATH))

    public BGPRoutingTable() {
        super();
        bestRoutes = new HashMap<String,PathAttributes>();
    }

    /*
        * This method is called when there is the need of updating the BGP Routing Table with the best paths from the RIB
        * The best path can be changed also based on the policies (like shortest path, lowest cost, etc)
     */
    public boolean updateTable(TopologyTable topologyTable) {
        /*
        ArrayList<PathAttributes> listRIB = topologyTable.getListRIB();
        boolean changed= false;
        System.out.println("Updating BGP Routing Table...");
        topologyTable.printTable();
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
         */
        Map<String,PathAttributes> topTable = topologyTable.getTopTable();
        boolean changed= false;
        System.out.println("Updating BGP Routing Table...");
        //topologyTable.printTable();
        //System.out.println(this.toString());

        for (Map.Entry<String, PathAttributes> entry : topTable.entrySet()) {
            String destIP = entry.getKey();
            PathSegments[] tmpPath= entry.getValue().getAS_PATH();

            if(bestRoutes.get(destIP) != null){
                // Compares existing table entry with the new one, deciding the best based on some criteria
                PathAttributes res = chooseBestEntry(bestRoutes.get(destIP), entry.getValue(), changed);
                if (res != null) {
                    bestRoutes.put(destIP, res);
                }
            } else {
                // Adds the new entry in the table since there is no entry for that destination IP
                bestRoutes.put(destIP, entry.getValue());
                changed = true;
            }

        }

        return changed;
    }

    public PathAttributes chooseBestEntry(PathAttributes oldEntry, PathAttributes newEntry, boolean changed){
        PathAttributes res = newEntry;
        //TODO: ADD THE DECIDING ALGORITHM BASED ON THE POLICIES WE CHOOSE
        // for now we just implement the shortest AS path
        if(oldEntry.getAS_PATH().length <= newEntry.getAS_PATH().length){
            res = oldEntry;
            changed = true;
        }
        return res;
    }

    public HashMap<String,PathAttributes> getBestRoutes() {
        return (HashMap<String, PathAttributes>) bestRoutes;
    }

    public void insertNewEntry(String destinationIp, String origin, PathSegments[] asPath, String nextHop) {
        bestRoutes.put(destinationIp, new PathAttributes(origin, asPath, nextHop));
    }
    @Override
    public String toString() {
        String result = "";
        for (String key : bestRoutes.keySet()) {
            result += "|Destination IP: " + key + " | Best Path: " + bestRoutes.get(key) + "; \n";
        }
        return result;
    }
}
