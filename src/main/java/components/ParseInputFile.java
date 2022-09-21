package components;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParseInputFile {

	static String FILENAME = "inputs.json";
	static String content;

	static void parseRouterInterfaces() throws IOException {
		String text = new String(Files.readAllBytes(Paths.get(FILENAME)), StandardCharsets.UTF_8);
		ArrayList<Router> routers = new ArrayList<Router>();

		JSONObject obj = new JSONObject(text);
		JSONArray routersArr = obj.getJSONArray("routers");
		
		for(int i = 0; i < routersArr.length(); i++) {
			String name = routersArr.getJSONObject(i).getString("name");
			ArrayList<RouterInterface> interfaces = new ArrayList<RouterInterface>();
			JSONArray interfacesArr = routersArr.getJSONObject(i).getJSONArray("interfaces");
			Router router = new Router(name);
			
			for(int j = 0; j < interfacesArr.length(); j++) {
				String ifName = interfacesArr.getJSONObject(j).getString("name");
				String ifAddr = interfacesArr.getJSONObject(j).getString("ipAddress");
				String ifMask = interfacesArr.getJSONObject(j).getString("subnetMask");
				
				RouterInterface interf = new RouterInterface(ifName, ifAddr, ifMask);
				interfaces.add(interf);
			}
			router.setInterfaces(interfaces);
			routers.add(router);
			
			router.printRouterInfo();
		}		
	}

	public static void main(String[] args) throws IOException {

		parseRouterInterfaces();
	}
}
