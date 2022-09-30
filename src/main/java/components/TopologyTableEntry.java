package components;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTableEntry {

	public String destinationIp;
	public String subnetMask;
	public String nextHop;
	public String interfaceName;
	public int metric;

	public TopologyTableEntry(String destinationIp, String subnetMask, String nextHop, String interfaceName, int metric){
		super();
		this.destinationIp=destinationIp;
		this.subnetMask=subnetMask;
		this.nextHop=nextHop;
		this.interfaceName=interfaceName;
		this.metric=metric;
	}

	public String getDestinationIp() {
		return destinationIp;
	}

	public void setDestinationIp(String destinationIp) {
		this.destinationIp = destinationIp;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public int getMetric() {
		return metric;
	}

	public void setMetric(int metric) {
		this.metric = metric;
	}



	@Override
	public String toString() {
		return "RoutingTable{" +
				"destinationIp='" + destinationIp + '\'' +
				", subnetMask='" + subnetMask + '\'' +
				", nextHop='" + nextHop + '\'' +
				", interfaceName='" + interfaceName + '\'' +
				", metric='" + metric + '\'' +
				'}';
	}
}
