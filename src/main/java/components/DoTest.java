package components;

public class DoTest {

	public static void main(String[] args) {

		String ip = "10.11.5.211";
		String ip2 = "10.11.5.2";
		String mask = "255.255.255.0";

		System.out.println(IpFunctions.getNetworkAddress(ip, mask));
		System.out.println(IpFunctions.getNetworkAddress(ip2, mask));
	}
}
