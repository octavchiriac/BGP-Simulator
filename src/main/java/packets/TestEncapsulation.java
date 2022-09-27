package packets;

public class TestEncapsulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sourceAddress = "1.7.255.128";
		String destinationAddress = "10.10.0.1";
		
		
		Packet tcpPacket = new TcpPacket(1027, 179, 7, 7, 7, 7, false, false, false, false, true, true, 0, 0, 0, "MUIE 123 MUMU23");
		String bitArrayTcp = tcpPacket.packetToBitArray();

		Packet ipPacket = new IpPacket(4, 5, 0, 15, 3, false, false, true, 0, 255, 6, 7, sourceAddress, destinationAddress, bitArrayTcp);
		String bitArrayIp = ipPacket.packetToBitArray();
		
		Packet hdlcPacket = new HdlcPacket("01111110", "11111111", "00000000", bitArrayIp, "00000000");
		String bitArrayHdlc = hdlcPacket.packetToBitArray();
		
		Packet hdlcPacket2 = new HdlcPacket(bitArrayHdlc);

		Packet ipPacket2 = new IpPacket(hdlcPacket2.getData());

		Packet tcpPacket2 = new TcpPacket(ipPacket2.getData());

		System.out.println(tcpPacket2.getData());
	}

}