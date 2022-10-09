package components;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTableEntry {

	public String NLRI;

	public TopologyTableEntry(){
		super();
	}




	@Override
	public String toString() {
		return "BGPTable{" + '}';
	}
}
