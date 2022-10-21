package components;

import multithread.ReceiveTcpPacket;
import multithread.ThreadPool;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;
import utils.TypeHandlers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Router implements Runnable {

    public String name;
    public ArrayList<RouterInterface> interfaces;
    public boolean isEnabled;
    public RoutingTableEntry routingTable;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    NeighborTable neighborTable;

    public Router(String name) {
        super();
        this.name = name;
        this.isEnabled = false; // TODO Implement enable/disable router
        this.neighborTable = new NeighborTable();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RouterInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(ArrayList<RouterInterface> interfaces2) {
        this.interfaces = interfaces2;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public RoutingTableEntry getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(RoutingTableEntry routingTable) {
        this.routingTable = routingTable;
    }
    
    private static RouterInterface getRouterInterfaceByIP(String ip) {
        for (Router r : Globals.routers) {
            for (RouterInterface i : r.getEnabledInterfaces()) {
                if (i.getIpAddress().equals(ip)) {
                    return i;
                }
            }
        }
        return null;
    }

    public ArrayList<RouterInterface> getEnabledInterfaces() {
        ArrayList<RouterInterface> enabledInterfaces = new ArrayList<RouterInterface>();
        for (RouterInterface i : interfaces) {
            if (i.isUp) {
                enabledInterfaces.add(i);
            }
        }
        return enabledInterfaces;
    }
    
    public ArrayList<String> getEnabledInterfacesAddresses() {
        ArrayList<String> enabledInterfaces = new ArrayList<String>();
        for (RouterInterface i : interfaces) {
            if (i.isUp) {
                enabledInterfaces.add(i.getIpAddress());
            }
        }
        return enabledInterfaces;
    }

    public void queuePacket(String bs) {
        System.out.println("[INFO] Packet queued at " + name + " at " + TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
        queue.add(bs);
    }
   
    public void populateNeighborTable () {
		for(RouterInterface i : this.getInterfaces()) {
			
			if(i.getDirectLink() != null) {
				RouterInterface neighborRouter = getRouterInterfaceByIP(i.getDirectLink());
				
				neighborTable.addNeighbor(neighborRouter.getIpAddress(), neighborRouter.getAs());
				
				System.out.println("[" + name  + "] Discovered neighbor " + neighborRouter.getIpAddress() + 
						" from AS " + neighborRouter.getAs());
			}
		}
    }

    public void printRouterInfo() {
        System.out.println(this.getName());
        for (RouterInterface inte : this.getInterfaces()) {
            System.out.println("Interface Name: " + inte.getName());
            System.out.println("IpAddress: " + inte.getIpAddress());
            System.out.println("Mask: " + inte.getSubnetMask());
            System.out.println("AS: " + inte.getAs());
            System.out.println("Direct link: " + inte.getDirectLink());
            System.out.println("\n");
        }
        System.out.println("########################");
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("[" + name  + "] Router starting");
        
        // Enabling router
        this.isEnabled = true;
        
        System.out.println("[" + name  + "] Router state : enabled");
        
        // Adding direct links to the neighbor table
        populateNeighborTable();

        // Listening for incoming messages
        while (isEnabled) {
            try {
                String msg;
                while ((msg = queue.poll()) != null) {

                    // Creating a task to handle the packet and adding it to the thread pool
                    ReceiveTcpPacket task = new ReceiveTcpPacket(msg);
                    ThreadPool.submit(task);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}










//public String sendTcpPacket(int sourcePort, int destinationPort, int seqNumber, int ackNumber, 
//String sourceIpAddress, String destinationIpAddress, String destinationMacAddress,
//boolean isSyn, boolean isAck) {
//
//Packet tcpPacket = new TcpPacket(sourcePort, destinationPort, seqNumber, ackNumber, 0, 0, false, isAck, 
//	false, false, isSyn, false, 0, 0, 0, "");
//String bitArrayTcp = tcpPacket.packetToBitArray();
//
//Packet ipPacket = new IpPacket(4, Globals.IP_HEADER_LENGTH, 0, bitArrayTcp.length()/8 + Globals.IP_HEADER_LENGTH, 
//	0, false, false, true, 0, 255, 6, 0, sourceIpAddress, destinationIpAddress, bitArrayTcp);
//String bitArrayIp = ipPacket.packetToBitArray();
//
//Packet hdlcPacket = new HdlcPacket("01111110", destinationMacAddress, "00000000", bitArrayIp, "00000000");
//
//return hdlcPacket.packetToBitArray();
//}
//
//public boolean receiveTcpPacket(String bitArrayHdlc, boolean isSyn, boolean isAck) {
//boolean hasError = false;
//boolean isFound = false;
//String interfaceName = "";
//String routerName = this.getName();
//
//Packet hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
//
//IpPacket ipPacket2 = new IpPacket(hdlcPacket2.getData());
//
//// verify that destination ip address is one of the router's interfaces
//for(RouterInterface inter : this.getEnabledInterfaces()) {
//if(inter.getIpAddress().equals(ipPacket2.getDestinationAddress())) {
//	isFound = true;
//	interfaceName = inter.getName();
//	System.out.println("Destination address is matched by interface " + interfaceName + " on router " + routerName);
//	break;
//}
//}
//if(!isFound) {
//hasError = true;
//System.err.println("Destination address " + ipPacket2.getDestinationAddress() + " is NOT matched any interface on router " + routerName);
//System.err.println("Package dropped!");
//} else {
//TcpPacket tcpPacket2 = new TcpPacket(ipPacket2.getData());
//
//if(isSyn) {
//	if(tcpPacket2.isSyn() == isSyn) {
//		System.out.println("SYN packet sucessfully received by router " + routerName + " on interface " + interfaceName);
//    } else {
//    	hasError = true;
//    	System.err.println("Packet received by router " + routerName + " on interface " + interfaceName + " expected SYN but does not contain it.");
//    }
//}
//
//if(isAck) {
//	if(tcpPacket2.isAck() == isAck) {
//	System.out.println("ACK packet sucessfully received by router " + routerName + " on interface " + interfaceName);
//    } else {
//    	hasError = true;
//    	System.err.println("Packet received by router " + routerName + " on interface " + interfaceName + " expected ACK but does not contain it.");
//   	}
//}
//}
//return hasError;
//}
//
//public static void establishTcpConection(Router sourceRouter, Router destinationRouter) {
//	
//	String bitArrayHdlc;
//	boolean hasError;
//
//	// Sending SYN packet from source router
//	System.out.println("[R1 -> R2]");
//	System.out.println("Sending SYN packet");
//	bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
//		 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
//		 Globals.DESTINATION_MAC_ADDRESS, true, false);
//	
//	// Receiving SYN packet to destination router
//	hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, true, false);
//	
//	if(!hasError) {
//		System.out.println("[R1 <- R2]");
//		System.out.println("Sending SYN+ACK packet");
//		bitArrayHdlc = destinationRouter.sendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1, destinationRouter.getEnabledInterfaces().get(2).getIpAddress(),
//				 sourceRouter.getEnabledInterfaces().get(1).getIpAddress(), 
//				 Globals.DESTINATION_MAC_ADDRESS, true, true);
//			
//		// Receiving SYN packet to destination router
//		hasError = sourceRouter.receiveTcpPacket(bitArrayHdlc, true, true);
//		
//		if(!hasError) {
//			System.out.println("[R1 -> R2]");
//			System.out.println("Sending ACK packet");
//			bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
//					 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
//					 Globals.DESTINATION_MAC_ADDRESS, false, true);
//				
//				// Receiving SYN packet to destination router
//				hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, false, true);
//				
//				if(!hasError) {
//					System.out.println("[R1 <-> R2]");
//					System.out.println("Connection established!");
//				}
//		}
//	}
//}
