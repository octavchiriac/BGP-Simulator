package components;

/*
 * "BGP Neighbor Table – table containing information about BGP neighbors"
 * This class is used to store the information of a neighbor router
 */
public class NeighborTableEntry {

	public String destinationIp;
	public String subnetMask;
	public String nextHop;
	public String interfaceName;
	public int metric;

	public NeighborTableEntry(String destinationIp, String subnetMask, String nextHop, String interfaceName, int metric){
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

	public String showIpBgpNeighbor(String destinationIp){
		return "show ip bgp neighbor " + destinationIp;
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
