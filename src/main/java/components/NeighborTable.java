package components;

import components.tblentries.NeighborTableEntry;

import java.util.ArrayList;
import java.util.List;

/*
 * "BGP Neighbor Table – table containing information about BGP neighbors"
 * This class is used to store the information of a neighbor router
 * https://techhub.hpe.com/eginfolib/networking/docs/switches/K-KA-KB/15-18/5998-8164_mrg/content/ch15s05.html
 */
public class NeighborTable {

	public List<NeighborTableEntry> neighborInfo;

	public NeighborTable(){
		super();
		neighborInfo = new ArrayList<>();
	}

	public void addNeighbor(String ip, String as, double trust){
		neighborInfo.add(new NeighborTableEntry(ip, as, trust));
	}

	public double getNeighborTrustByIp(String ip) {
		for (NeighborTableEntry entry : neighborInfo) {
			if (entry.getIp().equals(ip)) {
				return entry.getTrust();
			}
		}

		return 0;
	}

	public ArrayList<String> getNeighborIPs(){
		ArrayList<String> ips = new ArrayList<>();

		for (NeighborTableEntry entry : neighborInfo) {
			ips.add(entry.getIp());
		}

		return ips;
	}

	public List<NeighborTableEntry> getNeighborInfo() {
		return neighborInfo;
	}

	@Override
	public String toString() {
		return "NeighborTable [neighborInfo=" + neighborInfo + "]";
	}
}
