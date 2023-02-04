package components;

import components.tblentries.PathAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTable {

    public ArrayList<PathAttributes> listRIB; //stores all the possible paths
    List<Map<Integer, String>> NLRI; //stores the NLRI, the prefix and the length

    public Map<String, PathAttributes> topTable; //topology table composed by (DEST_IP,(NEXT_HOP,ORIGIN,AS_PATH))

    public TopologyTable() {
        super();
        listRIB = new ArrayList<>();
        NLRI = new ArrayList<>();
        topTable = new HashMap<>();
    }

    //inserting in the new topology table by using also the NLRI as parameter
    public void insertEntry(String destIp, PathAttributes pathAttributes) {
        topTable.put(destIp, pathAttributes);
    }

    public Map<String, PathAttributes> getTopTable() {
        return topTable;
    }

    //remove the entry based on the ip (which corresponds to the next hop)
    public boolean removeEntryByIp(String ip) {
        try {
            boolean removed = false;
            //Remove the entry from the topology table if the destination IP or the NEXTHOP corresponds to the withdrawn IP
            //IF the entry in TopTable is NULL, then throws an exception, and it returns false
            for (Map.Entry<String, PathAttributes> entry : topTable.entrySet()) {
                if (entry.getKey().equals(ip) || entry.getValue().getNEXT_HOP().equals(ip)) {
                    topTable.remove(entry.getKey());
                    removed = true;
                }
            }
            return removed;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "BGPTable{" + '}';
    }
}
