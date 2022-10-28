package components;

import java.util.ArrayList;

/*
 * "BGP Routing Table – table containing information about the best path to each destination network"
 * BGP Routing Table – the main IP routing tables that contains only the best routes from BGP Table.
 * After BGP has selected the best path to a network, that path is added to the main IP routing table.
 */
public class RoutingTableEntry {

	public String destinationIp;
	public String origin;
	public ArrayList<String> asPath;
	public String nextHop;


	public RoutingTableEntry(){
		super();
	}
}
