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



    public BGPRoutingTable() {
        super();

    }

    public void updateTable(TopologyTable topologyTable) {
        //TODO: implement update of the table by searching the best path on the TopologyTable
        //ArrayList<TopologyTableEntry> listRIB=new ArrayList<TopologyTableEntry>();
        //listRIB=topologyTable.getListRIB();

    }


}
