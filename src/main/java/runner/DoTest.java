package runner;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map.Entry;

import multithread.SendTcpPacket;
import multithread.ThreadPool;
import components.Globals;
import components.Router;
import utils.ParseInputFile;

public class DoTest {
	
	
	public static void establishTcpConection(Router sourceRouter, Router destinationRouter) {
		
		String bitArrayHdlc;
		boolean hasError;

		// Sending SYN packet from source router
		System.out.println("[R1 -> R2]");
		System.out.println("Sending SYN packet");
		bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
			 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
			 Globals.DESTINATION_MAC_ADDRESS, true, false);
		
		// Receiving SYN packet to destination router
		hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, true, false);
		
		if(!hasError) {
			System.out.println("[R1 <- R2]");
			System.out.println("Sending SYN+ACK packet");
			bitArrayHdlc = destinationRouter.sendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1, destinationRouter.getEnabledInterfaces().get(2).getIpAddress(),
					 sourceRouter.getEnabledInterfaces().get(1).getIpAddress(), 
					 Globals.DESTINATION_MAC_ADDRESS, true, true);
				
			// Receiving SYN packet to destination router
			hasError = sourceRouter.receiveTcpPacket(bitArrayHdlc, true, true);
			
			if(!hasError) {
				System.out.println("[R1 -> R2]");
				System.out.println("Sending ACK packet");
				bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
						 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
						 Globals.DESTINATION_MAC_ADDRESS, false, true);
					
					// Receiving SYN packet to destination router
					hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, false, true);
					
					if(!hasError) {
						System.out.println("[R1 <-> R2]");
						System.out.println("Connection established!");
					}
			}
		}
	}

    public static void main(String[] args) throws IOException, InterruptedException {

        // TEST PARSING INPUT FILE
        ParseInputFile parseInput = new ParseInputFile();
        parseInput.parseRouterInterfaces();
        parseInput.parseDirectLinks();
//
//        for (int i = 0; i < 3; i++) {
//            Globals.routers.get(i).printRouterInfo();
//        }
//
        // ROUTERS NEED TO BE RUN AT STARTUP AFTER FILE PARSING
        for (Router r : Globals.routers) {
            Thread t = new Thread(r);
            t.start();
        }
        
        //TODO how to wait between fors ??
        
        Router r1 = Globals.routers.get(0);
        Router r2 = Globals.routers.get(1);
        Router r3 = Globals.routers.get(2);
        
        java.util.List<java.util.Map.Entry<Router,Router>> connectedRouters = new java.util.ArrayList<>();
        Entry<Router,Router> pair1 = new AbstractMap.SimpleEntry<>(r1,r2);
        Entry<Router,Router> pair2 = new AbstractMap.SimpleEntry<>(r2,r3);
        Entry<Router,Router> pair3 = new AbstractMap.SimpleEntry<>(r3,r1);
        connectedRouters.add(pair1);
        connectedRouters.add(pair2);
        connectedRouters.add(pair3);
       
        ThreadPool.run();
        
        for(Entry<Router, Router> pair : connectedRouters) {
          SendTcpPacket task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0, 
        		  pair.getKey().getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
  				 pair.getValue().getEnabledInterfaces().get(1).getIpAddress(), Globals.DESTINATION_MAC_ADDRESS, true, false);
        
          ThreadPool.submit(task);
        }
        
        for(Entry<Router, Router> pair : connectedRouters) {
            SendTcpPacket task = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1, 
            		pair.getValue().getEnabledInterfaces().get(1).getIpAddress(),
            		pair.getKey().getEnabledInterfaces().get(1).getIpAddress(), 
   				 Globals.DESTINATION_MAC_ADDRESS, true, true);
            ThreadPool.submit(task);
          }
        
        for(Entry<Router, Router> pair : connectedRouters) {
            SendTcpPacket task = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1, 
            		pair.getKey().getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
            		pair.getValue().getEnabledInterfaces().get(1).getIpAddress(), 
					 Globals.DESTINATION_MAC_ADDRESS, false, true);
            ThreadPool.submit(task);
          }
//        ThreadPool.stop();
    }
}
