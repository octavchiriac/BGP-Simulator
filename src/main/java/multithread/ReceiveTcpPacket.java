package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;

public class ReceiveTcpPacket implements Runnable {
	String bitArrayHdlc;


	public ReceiveTcpPacket(String bitArrayHdlc) {
		super();
		this.bitArrayHdlc = bitArrayHdlc;
	}
	
	private static Router getRouterByIP(String ip) {
        for (Router r : Globals.routers) {
            for (RouterInterface i : r.getEnabledInterfaces()) {
                if (i.getIpAddress().equals(ip)) {
                    return r;
                }
            }
        }
        return null;
    }
	
	public void receiveTcpPacket(String bitArrayHdlc) throws Exception {
		boolean isFound = false;
		String interfaceName = "";
	
		Packet hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
		IpPacket ipPacket2 = new IpPacket(hdlcPacket2.getData());
		TcpPacket tcpPacket2 = new TcpPacket(ipPacket2.getData());
		
		String destinationIpAddress = ipPacket2.getDestinationAddress();
        Router dest = getRouterByIP(destinationIpAddress);
        String destRouterName = dest.getName();
        
        String sourceIpAddress = ipPacket2.getSourceAddress();
        Router src = getRouterByIP(sourceIpAddress);
        String srcRouterName = src.getName();
        
        if (ipPacket2.getTimeToLive() == 0) {
            throw new Exception("[" + srcRouterName + " -> " + destRouterName + "] TTL expired. Packet dropped!");
        } else {
	        // verify that destination ip address is one of the router's interfaces
			for(RouterInterface inter : dest.getEnabledInterfaces()) {
				if(inter.getIpAddress().equals(destinationIpAddress)) {
					isFound = true;
					interfaceName = inter.getName();
					System.out.println("[" + srcRouterName + " -> " + destRouterName + "] Destination address is matched by interface " + interfaceName);
					break;
				}
			}
			if(!isFound) {
	    		System.err.println("[" + srcRouterName + " -> " + destRouterName + "] Destination address " + destinationIpAddress + " is NOT matched any interface");
	    		
	    		//TODO search in table for destination
	    		
	    		//TODO what to do here if you don't find the router in the table? do you broadcast?
	    		ipPacket2.decreaseTimeToLive();
	    		
	    		Packet hdlcPacket = new HdlcPacket("01111110", Globals.DESTINATION_MAC_ADDRESS, "00000000", ipPacket2.packetToBitArray(), "00000000");
	    		
	    		SendTcpPacket task = new SendTcpPacket(hdlcPacket.packetToBitArray(), ipPacket2.getSourceAddress(), 
	    				ipPacket2.getDestinationAddress(), tcpPacket2.isSyn(), tcpPacket2.isAck());
	    		ThreadPool.submit(task);
	
	    	} else {
	    		 if(tcpPacket2.isSyn() && tcpPacket2.isAck()) {
	         		System.out.println("[" + srcRouterName + " -> " + destRouterName + "] SYN + ACK packet sucessfully received on interface " + interfaceName);
	 	        } else if(tcpPacket2.isSyn()) {
	        		System.out.println("[" + srcRouterName + " -> " + destRouterName + "] SYN packet sucessfully received on interface " + interfaceName);
		        } else if(tcpPacket2.isAck()) {
		        	System.out.println("[" + srcRouterName + " -> " + destRouterName + "] ACK packet sucessfully received on interface " + interfaceName);
		        	
		        	System.out.println("[" + srcRouterName + " -> " + destRouterName + "] TCP connection established!");
		        }
	    	}
        }
	}

	@Override
	public void run() {
		
		try {
			this.receiveTcpPacket(bitArrayHdlc);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
