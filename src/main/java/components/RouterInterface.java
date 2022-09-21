package components;

import java.util.ArrayList;

public class RouterInterface {

	public String name;
	public String ipAddress;
	public String subnetMask;
	public boolean isUp;
	public ArrayList<String> directLinks;

	public RouterInterface(String name, String ipAddress, String subnetMask) {
		super();
		this.name = name;
		this.ipAddress = ipAddress;
		this.subnetMask = subnetMask;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public void setSubnetMask(String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	public ArrayList<String> getDirectLinks() {
		return directLinks;
	}

	public void setDirectLinks(ArrayList<String> directLinks) {
		this.directLinks = directLinks;
	}

}
