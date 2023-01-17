package components;

import java.util.ArrayList;
import java.util.Map;

public class Globals {

	public static ArrayList<Router> routers;
	public static int IP_HEADER_LENGTH = 5;
	public static String DESTINATION_MAC_ADDRESS = "11111111";
	public static int TCP_PORT = 179;
	public static int UDP_PORT = 1027;
	public static int nrRoutersStarted = 0;
	public static ArrayList<String> routerNames = new ArrayList<>();
	public static Map<String, Object> linkMap;
	public static int BGP_VERSION = 4;
	public static int HOLD_TIMER = 15000;
	public static int NOTIFICATION_DISCONNECT = 6;
	public static final Object lock = new Object();
}
