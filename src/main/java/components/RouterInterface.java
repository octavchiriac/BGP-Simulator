package components;

public class RouterInterface {

	private String name;
	private String ipAddress;
	private String subnetMask;
	private boolean isUp;
	private String directLink;
	private String as;
	BGPStates state;
	double totalTrust;

	public RouterInterface(String name, String ipAddress, String subnetMask, String as) {
		super();
		this.name = name;
		this.ipAddress = ipAddress;
		this.subnetMask = subnetMask;
		this.as = as;
		this.isUp = true;
		this.state = BGPStates.Idle;
		this.totalTrust = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BGPStates getState() {
		return state;
	}

	public void setState(BGPStates state) {
		this.state = state;
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

	public double getTotalTrust() {
		return totalTrust;
	}

	public void setTotalTrust(double totalTrust) {
		this.totalTrust = totalTrust;
	}
}
