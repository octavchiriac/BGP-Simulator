package components;

import java.util.ArrayList;

/*
 * "BGP Neighbor Table – table containing information about BGP neighbors"
 * This class is used to store the information of a neighbor router
 * https://techhub.hpe.com/eginfolib/networking/docs/switches/K-KA-KB/15-18/5998-8164_mrg/content/ch15s05.html
 */
public class NeighborTableEntry {

	public ArrayList<String> neighborsIp;
	public ArrayList<String> neighborsAs;

	public NeighborTableEntry(){
		super();
		neighborsIp = new ArrayList<String>();
		neighborsAs = new ArrayList<String>();
	}

	public void addNeighbor(String ip, String as){
		neighborsIp.add(ip);
		neighborsAs.add(as);
	}

	public void editNeighbor(String ip, String newIp, String newAs){
		int index = neighborsIp.indexOf(ip);
		neighborsIp.set(index, newIp);
		neighborsAs.set(index, newAs);
	}

	public void deleteNeighbor(String ip){
		int index = neighborsIp.indexOf(ip);
		neighborsIp.remove(index);
		neighborsAs.remove(index);
	}

	public ArrayList<String> getNeighborsIp() {
		return neighborsIp;
	}


	@Override
	public String toString() {
		//return "NeighborTableEntry [neighborsIp=" + neighborsIp + ", neighborsAs=" + neighborsAs + "]";
		String result = "NeighborTableEntry {";
		for(int i=0; i<neighborsIp.size(); i++) {
			result += "Neighbor IP: " + neighborsIp.get(i) + " Neighbor AS: " + neighborsAs.get(i) + ",";
		}
		result += "}";
		return result;
	}
}
