package components;

import java.util.ArrayList;

public class Router {

	public String name;
	public ArrayList<RouterInterface> interfaces;
	public boolean isEnabled;
	public RoutingTableEntry routingTable;

	public Router(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<RouterInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<RouterInterface> interfaces2) {
		this.interfaces = interfaces2;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public RoutingTableEntry getRoutingTable() {
		return routingTable;
	}

	public void setRoutingTable(RoutingTableEntry routingTable) {
		this.routingTable = routingTable;
	}
	
	public void printRouterInfo() {
		System.out.println(this.getName());
		for(RouterInterface inte : this.getInterfaces()) {
			System.out.println("Interface Name: " + inte.getName());
			System.out.println("IpAddress: " + inte.getIpAddress());
			System.out.println("Mask: " + inte.getSubnetMask());
			System.out.println("AS: " + inte.getAs());
			System.out.println("Direct link: " + inte.getDirectLink());
			System.out.println("\n");
		}
		System.out.println("########################");
	}

}
