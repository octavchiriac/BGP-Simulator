package components;

import java.io.IOException;

public class DoTest {

	public static void main(String[] args) throws IOException {
		
		// initialize routers array with elements from the input file
		ParseInputFile parseInput = new ParseInputFile();
		parseInput.parseRouterInterfaces();
		parseInput.parseDirectLinks();
		
		for(int i = 0; i < 3; i++) {
			Globals.routers.get(i).printRouterInfo();	
		}
	}
}
