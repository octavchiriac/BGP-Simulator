package components;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * "BGP Neighbor Table – table containing information about BGP neighbors"
 * This class is used to store the information of a neighbor router
 * https://techhub.hpe.com/eginfolib/networking/docs/switches/K-KA-KB/15-18/5998-8164_mrg/content/ch15s05.html
 */
public class NeighborTable {

	// pair neighborIp, neighborAS
	public HashMap<String,String> neighborInfo;

	public NeighborTable(){
		super();
		neighborInfo = new HashMap<String,String>();
	}

	public void addNeighbor(String ip, String as){
		neighborInfo.put(ip, as);
	}

	public String getNeighborAS(String ip){
		return neighborInfo.get(ip);
	}

	public ArrayList<String> getNeighborIPs(){
		ArrayList<String> ips = new ArrayList<String>();
		for(String ip : neighborInfo.keySet()){
			ips.add(ip);
		}
		return ips;
	}

	public void editNeighbor(String ip, String newIp, String newAs){
		neighborInfo.remove(ip);
		neighborInfo.put(newIp, newAs);
	}

	public void deleteNeighbor(String ip){
		neighborInfo.remove(ip);
	}

	public HashMap<String, String> getNeighborInfo() {
		return neighborInfo;
	}


	@Override
	public String toString() {
		return "NeighborTable [neighborInfo=" + neighborInfo + "]";
	}
}
