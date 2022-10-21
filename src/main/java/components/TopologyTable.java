package components;

import java.util.ArrayList;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTable{

	public String prefix; //Prefix of the route.
	public int prefixLength; //Prefix length of the route.
	public String advertisedRouterId; //IP address of the device that advertised the route.
	public ArrayList<Route> listRIB; //BGP Routing Information Base (RIB) – a table containing information about the bst path to each destination network.
	public ArrayList<NLRI> listNLRI;

	public TopologyTable(){
		super();
		listRIB = new ArrayList<Route>();
		listNLRI= new ArrayList<NLRI>();
	}

	//NEXT-HOP for NLRI is added according to the route in the RIB.
	public void addNextHopNLRI(String length, String destinationAddress){
		String nextHop=listRIB.get(listRIB.indexOf(destinationAddress)).getNextHop();
		listNLRI.add(new NLRI(length,nextHop));
	}

	public void addRoute(Route route){
		listRIB.add(route);
	}

	public void removeRoute(Route route){
		listRIB.remove(route);
	}

	public void editRoute(Route route){
		listRIB.set(listRIB.indexOf(route), route);
	}
	public ArrayList<Route> getRIB(){
		return listRIB;
	}


	@Override
	public String toString() {
		return "BGPTable{" + '}';
	}
}
