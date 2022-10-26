package packets;

import utils.BinaryFunctions;

public class TcpPacket extends Packet{

	private int sourcePort;
    private int destinationPort;
    private int sequenceNumber;
    private int acknowledgementNumber;
    private int dataOffset;
    private int reserved;
    private boolean urg;
    private boolean ack;
    private boolean psh;
    private boolean rst;
    private boolean syn;
    private boolean fin;
    private int windowSize;
    private int checksum;
    private int urgentPointer;
	
	public TcpPacket(int sourcePort, int destinationPort, int sequenceNumber, int acknowledgementNumber,
			int dataOffset, int reserved, boolean urg, boolean ack, boolean psh, boolean rst, boolean syn,
			boolean fin, int windowSize, int checksum, int urgentPointer, String data) {
		super();
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgementNumber = acknowledgementNumber;
		this.dataOffset = dataOffset;
		this.reserved = reserved;
		this.urg = urg;
		this.ack = ack;
		this.psh = psh;
		this.rst = rst;
		this.syn = syn;
		this.fin = fin;
		this.windowSize = windowSize;
		this.checksum = checksum;
		this.urgentPointer = urgentPointer;
		this.data = data;
	}
	
	public TcpPacket(String bitsArray) {
		super();
		this.sourcePort = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 16, Integer.class);
		this.destinationPort = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 16, 16, Integer.class);
		this.sequenceNumber = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 32, 32, Integer.class);
		this.acknowledgementNumber = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 64, 32, Integer.class);
		this.dataOffset = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 96, 4, Integer.class);
		this.reserved = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 100, 6, Integer.class);
		this.urg = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 106, 1, Boolean.class);
		this.ack = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 107, 1, Boolean.class);
		this.psh = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 108, 1, Boolean.class);
		this.rst = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 109, 1, Boolean.class);
		this.syn = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 110, 1, Boolean.class);
		this.fin = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 111, 1, Boolean.class);
		this.windowSize = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 112, 16, Integer.class);
		this.checksum = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 128, 16, Integer.class);
		this.urgentPointer = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 144, 16, Integer.class);
		this.data = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 160, -1, String.class);
	}
	
	public String packetToBitArray() {
		String bitsArray = 
				BinaryFunctions.toBitsArray(this.sourcePort, 16) + 
				BinaryFunctions.toBitsArray(this.destinationPort, 16) + 
				BinaryFunctions.toBitsArray(this.sequenceNumber, 32) + 
				BinaryFunctions.toBitsArray(this.acknowledgementNumber, 32) + 
				BinaryFunctions.toBitsArray(this.dataOffset, 4) + 
				BinaryFunctions.toBitsArray(this.reserved, 6) + 
				BinaryFunctions.toBitsArray(this.urg, 1) + 
				BinaryFunctions.toBitsArray(this.ack, 1) + 
				BinaryFunctions.toBitsArray(this.psh, 1) + 
				BinaryFunctions.toBitsArray(this.rst, 1) + 
				BinaryFunctions.toBitsArray(this.syn, 1) + 
				BinaryFunctions.toBitsArray(this.fin, 1) + 
				BinaryFunctions.toBitsArray(this.windowSize, 16) + 
				BinaryFunctions.toBitsArray(this.checksum, 16) + 
				BinaryFunctions.toBitsArray(this.urgentPointer, 16) + 
				BinaryFunctions.toBitsArray(this.data, 0); 
		
		return bitsArray;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public boolean isAck() { return ack; }

	public boolean isSyn() {
		return syn;
	}

	public boolean isPsh() { return psh; }

	public boolean isRst() { return rst; }

	@Override
	public String toString() {
		return "TcpPacket{" +
				"sourcePort=" + sourcePort +
				", destinationPort=" + destinationPort +
				", sequenceNumber=" + sequenceNumber +
				", acknowledgementNumber=" + acknowledgementNumber +
				", dataOffset=" + dataOffset +
				", reserved=" + reserved +
				", urg=" + urg +
				", ack=" + ack +
				", psh=" + psh +
				", rst=" + rst +
				", syn=" + syn +
				", fin=" + fin +
				", windowSize=" + windowSize +
				", checksum=" + checksum +
				", urgentPointer=" + urgentPointer +
				", data='" + data + '\'' +
				", id=" + id +
				'}';
	}
}
