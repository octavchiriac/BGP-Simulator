package components;

import java.util.ArrayList;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTable{

	public String destinationIp;
	public String nextHop;
	public String metric;
	public ArrayList<String> pathAS;

	//public ArrayList<String> listRIB; //stores all the possible paths

	public TopologyTable(){
		super();
	}


	@Override
	public String toString() {
		return "BGPTable{" + '}';
	}
}
