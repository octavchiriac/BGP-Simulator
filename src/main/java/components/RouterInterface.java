package components;

public class RouterInterface {

	public String name;
	public String ipAddress;
	public String subnetMask;
	public boolean isUp;
	public String directLink;
	public String as;

	public RouterInterface(String name, String ipAddress, String subnetMask, String as) {
		super();
		this.name = name;
		this.ipAddress = ipAddress;
		this.subnetMask = subnetMask;
		this.as = as;
		this.isUp = true;
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

	public String getDirectLink() {
		return directLink;
	}

	public void setDirectLink(String directLink) {
		this.directLink = directLink;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

	public String getAs() {
		return as;
	}

	public void setAs(String as) {
		this.as = as;
	}
	
}
