package packets;

public class TestEncapsulation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TcpPacket packet = new TcpPacket(1027, 179, 7, 7, 7, 7, false, false, false, false, true, true, 0, 0, 0, "TEST DATA 123asdas");
	
		String bitArray = packet.packetToBitArray();
		TcpPacket packet2 = new TcpPacket(bitArray);
		
		System.out.println(packet2.getData());
		System.out.println(packet2.getDestinationPort());
		System.out.println(packet2.isAck());
		System.out.println(packet2.isSyn());
	}

}
