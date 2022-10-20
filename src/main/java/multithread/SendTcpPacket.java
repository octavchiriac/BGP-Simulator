package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;

public class SendTcpPacket implements Runnable{

	int sourcePort;
	int destinationPort;
	int seqNumber;
	int ackNumber;
	String sourceIpAddress;
	String destinationIpAddress; 
	String destinationMacAddress; 
	boolean isSyn;
	boolean isAck;
	String bitStream;

	public SendTcpPacket(int sourcePort, int destinationPort, int seqNumber, int ackNumber, String sourceIpAddress,
			String destinationIpAddress, String destinationMacAddress, boolean isSyn, boolean isAck) {
		super();
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.seqNumber = seqNumber;
		this.ackNumber = ackNumber;
		this.sourceIpAddress = sourceIpAddress;
		this.destinationIpAddress = destinationIpAddress;
		this.destinationMacAddress = destinationMacAddress;
		this.isSyn = isSyn;
		this.isAck = isAck;
	}
	
	public SendTcpPacket(String bitStream, String sourceIpAddress, String destinationIpAddress, boolean isSyn, boolean isAck) {
		this.bitStream = bitStream;
		this.sourceIpAddress = sourceIpAddress;
		this.destinationIpAddress = destinationIpAddress;
		this.isSyn = isSyn;
		this.isAck = isAck;
	}
	
	 public String sendTcpPacket(int sourcePort, int destinationPort, int seqNumber, int ackNumber, 
	    		String sourceIpAddress, String destinationIpAddress, String destinationMacAddress,
	    		boolean isSyn, boolean isAck) {

		    Packet tcpPacket = new TcpPacket(sourcePort, destinationPort, seqNumber, ackNumber, 0, 0, false, isAck, 
		    		false, false, isSyn, false, 0, 0, 0, "");
		    String bitArrayTcp = tcpPacket.packetToBitArray();
		    
		    Packet ipPacket = new IpPacket(4, Globals.IP_HEADER_LENGTH, 0, bitArrayTcp.length()/8 + Globals.IP_HEADER_LENGTH, 
		    		0, false, false, true, 0, 255, 6, 0, sourceIpAddress, destinationIpAddress, bitArrayTcp);
	        String bitArrayIp = ipPacket.packetToBitArray();
	        
	        Packet hdlcPacket = new HdlcPacket("01111110", destinationMacAddress, "00000000", bitArrayIp, "00000000");
	        
	        return hdlcPacket.packetToBitArray();
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

	@Override
	public void run() {
		String packetType = null;
        Router src = getRouterByIP(sourceIpAddress);
        Router dest = getRouterByIP(destinationIpAddress);
        
        if(isSyn && isAck) {
        	packetType = "SYN + ACK";
        } else if(isAck) {
        	packetType = "ACK";
        } else if (isSyn) {
        	packetType = "SYN";
        }
        System.out.println("[" + src.getName() + " -> " + dest.getName() + "] Sending " + packetType + " packet");
        if(bitStream == null) {
	        bitStream = this.sendTcpPacket(sourcePort, destinationPort, seqNumber, ackNumber,
					sourceIpAddress, destinationIpAddress, destinationMacAddress, isSyn, isAck);
        }
        
		// queueing packet at destination router
		dest.queuePacket(bitStream);
	}

}
