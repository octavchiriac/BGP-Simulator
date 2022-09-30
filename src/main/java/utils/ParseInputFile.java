package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import components.Globals;
import components.Router;
import components.RouterInterface;

public class ParseInputFile {

	static String FILENAME = "inputs.json";
	static String content;

	public void parseRouterInterfaces() throws IOException {
		content = new String(Files.readAllBytes(Paths.get(FILENAME)), StandardCharsets.UTF_8);
		Globals.routers = new ArrayList<Router>();

		JSONObject obj = new JSONObject(content);
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
				String ifAS = interfacesArr.getJSONObject(j).getString("as");
				
				RouterInterface interf = new RouterInterface(ifName, ifAddr, ifMask, ifAS);
				interfaces.add(interf);
			}
			router.setInterfaces(interfaces);
			
			Globals.routers.add(router);			
		}		
	}
	
	public void parseDirectLinks() {
		
		JSONObject obj = new JSONObject(content);
		JSONObject links = obj.getJSONObject("links");
		Map<String, Object> linkMap = links.toMap();
		Map<Object, String> reverseLinkMap = IpFunctions.reverseMap(linkMap);

		for(Router r : Globals.routers) {
			for(RouterInterface i : r.getInterfaces()) {
				if(linkMap.containsKey(i.getIpAddress())) {
					i.setDirectLink(linkMap.get(i.getIpAddress()).toString());
				}
				if(reverseLinkMap.containsKey(i.getIpAddress())) {
					i.setDirectLink(reverseLinkMap.get(i.getIpAddress()).toString());
				}
			}
		}
	}
}
