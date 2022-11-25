package components;

import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * BGP Table (also known as BGP topology table, BGP RIB) contains the network layer reachability information (NLRI)
 * learned in compliance with BGP and NLRI attributes (path attribute, PA) corresponding to these path.
 * Essentially, NLRI is a prefix and its length. BGP table contains all the routes from all the neighbors,
 * several routes to the same network with different attributes.
 */
public class TopologyTable{

	//public ArrayList<TopologyTableEntry> listRIB; //stores all the possible paths
	public  ArrayList<PathAttributes> listRIB; //stores all the possible paths
	List<Map<Integer, String>> NLRI; //stores the NLRI, the prefix and the length

	public TopologyTable(){
		super();
		//listRIB = new ArrayList<TopologyTableEntry>();
		listRIB = new ArrayList<PathAttributes>();
		NLRI = new ArrayList<Map<Integer, String>>();
	}

	public void inserEntrytNLRI(Map<Integer, String> entry){
		NLRI.add(entry);
	}

	public List<Map<Integer, String>> getNLRI(){
		return NLRI;
	}

	public void insertNewEntry(PathAttributes pathAttributes){
		listRIB.add(pathAttributes);
	}

	public void insertNewEntry(String origin, PathSegments[] asPath, String nextHop){
		listRIB.add(new PathAttributes(origin, asPath, nextHop));
	}

	public ArrayList<PathAttributes> getListRIB() {
		return listRIB;
	}

	public void setListRIB(ArrayList<PathAttributes> listRIB) {
		this.listRIB = listRIB;
	}

	public void removeEntry(PathAttributes pathAttributes){
		listRIB.remove(pathAttributes);
	}

	//remove the entry based on the ip (which corresponds to the next hop)
	public boolean removeEntryByIp(String ip){
		boolean removed = false;
		for(int i = 0; i < listRIB.size(); i++){
			if(listRIB.get(i).getNEXT_HOP().equals(ip)){
				listRIB.remove(i);
				removed = true;
			}
		}
		return removed;
	}

	public void printTable(){
		for (PathAttributes pathAttributes : listRIB) {
			System.out.println(pathAttributes.toString());
		}
	}

	/*
	public void insertNewEntry(String destinationIp, String nextHop, String metric, ArrayList<String> pathAS){
		listRIB.add(new TopologyTableEntry(destinationIp, nextHop, metric, pathAS));
	}

	public void insertNewEntry(TopologyTableEntry entry){
		listRIB.add(entry);
	}

	public void removeEntry(TopologyTableEntry entry){
		listRIB.remove(entry);
	}

	public ArrayList<TopologyTableEntry> getListRIB() {
		return listRIB;
	}
	*/

	@Override
	public String toString() {
		return "BGPTable{" + '}';
	}
}
