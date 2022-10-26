package multithread;

import components.Globals;
import components.Router;
import components.RouterInterface;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;

import static components.Router.getRouterByIP;

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
	boolean isPsh;
	boolean isRst;
	String bitStream;
	String data;

	public SendTcpPacket(int sourcePort, int destinationPort, int seqNumber, int ackNumber,
						 String sourceIpAddress, String destinationIpAddress, String destinationMacAddress,
						 boolean isSyn, boolean isAck, boolean isPsh, boolean isRst, String data) {
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
		this.isPsh = isPsh;
		this.isRst = isRst;
		this.data = data;
	}
	
	public SendTcpPacket(String bitStream, String sourceIpAddress, String destinationIpAddress,
						 boolean isSyn, boolean isAck, boolean isPsh, boolean isRst, String data) {
		this.bitStream = bitStream;
		this.sourceIpAddress = sourceIpAddress;
		this.destinationIpAddress = destinationIpAddress;
		this.isSyn = isSyn;
		this.isAck = isAck;
		this.isPsh = isPsh;
		this.isRst = isRst;
		this.data = data;
	}
	
	 public String sendTcpPacket(int sourcePort, int destinationPort, int seqNumber, int ackNumber, 
	    		String sourceIpAddress, String destinationIpAddress, String destinationMacAddress,
	    		boolean isSyn, boolean isAck, boolean isPsh, boolean isRst, String data) {

		    Packet tcpPacket = new TcpPacket(sourcePort, destinationPort, seqNumber, ackNumber, 0, 0, false, isAck,
					isPsh, isRst, isSyn, false, 0, 0, 0, data);
		    String bitArrayTcp = tcpPacket.packetToBitArray();
		    
		    Packet ipPacket = new IpPacket(4, Globals.IP_HEADER_LENGTH, 0, bitArrayTcp.length()/8 + Globals.IP_HEADER_LENGTH, 
		    		0, false, false, true, 0, 255, 6, 0, sourceIpAddress, destinationIpAddress, bitArrayTcp);
	        String bitArrayIp = ipPacket.packetToBitArray();
	        
	        Packet hdlcPacket = new HdlcPacket("01111110", destinationMacAddress, "00000000", bitArrayIp, "00000000");
	        
	        return hdlcPacket.packetToBitArray();
		}

	@Override
	public void run() {
		String packetType = null;
        Router src = getRouterByIP(sourceIpAddress);
        Router dest = getRouterByIP(destinationIpAddress);
        
        if(isSyn && isAck) {
        	packetType = "SYN + ACK";
		} else if (isPsh && isAck) {
			packetType = "PSH + ACK";
        } else if(isAck) {
        	packetType = "ACK";
        } else if (isSyn) {
        	packetType = "SYN";
		} else if (isRst) {
			packetType = "RST";
		}
        System.out.println("[" + src.getName() + " -> " + dest.getName() + "] Sending " + packetType + " packet");
        if(bitStream == null) {
	        bitStream = this.sendTcpPacket(sourcePort, destinationPort, seqNumber, ackNumber,
					sourceIpAddress, destinationIpAddress, destinationMacAddress,
					isSyn, isAck, isPsh, isRst, data);
        }
        
		// queueing packet at destination router
		dest.queuePacket(bitStream);
	}

}
