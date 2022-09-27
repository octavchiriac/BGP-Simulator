package packets;

import java.net.Inet4Address;

public class IpPacket extends Packet{

	private int version;
	private int headerLength;
	private int seviceType;
	private int totalLength;
	private int identification;
	private boolean reserved;
	private boolean df;
	private boolean mf;
	private int fragmentOffset;
	private int timeToLive;
	private int protocol;
	private int headerChecksum;
	private String sourceAddress;
	private String destinationAddress;
	
	public IpPacket(int version, int headerLength, int seviceType, int totalLength, int identification,
			boolean reserved, boolean df, boolean mf, int fragmentOffset, int timeToLive, int protocol,
			int headerChecksum, String sourceAddress, String destinationAddress, String data) {
		super();
		this.version = version;
		this.headerLength = headerLength;
		this.seviceType = seviceType;
		this.totalLength = totalLength;
		this.identification = identification;
		this.reserved = reserved;
		this.df = df;
		this.mf = mf;
		this.fragmentOffset = fragmentOffset;
		this.timeToLive = timeToLive;
		this.protocol = protocol;
		this.headerChecksum = headerChecksum;
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destinationAddress;
		this.data = data;
	}
	
	public IpPacket(String bitsArray) {
		super();
		this.version = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 4, Integer.class);
		this.headerLength = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 4, 8, Integer.class);
		this.seviceType = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 8, 8, Integer.class);
		this.totalLength = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 16, 16, Integer.class);
		this.identification = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 32, 16, Integer.class);
		this.reserved = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 48, 1, Boolean.class);
		this.df = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 49, 1, Boolean.class);
		this.mf = (boolean) BinaryFunctions.bitsArrayToObject(bitsArray, 50, 1, Boolean.class);
		this.fragmentOffset = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 51, 13, Integer.class);
		this.timeToLive = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 64, 8, Integer.class);
		this.protocol = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 72, 8, Integer.class);
		this.headerChecksum = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 80, 16, Integer.class);
		this.sourceAddress = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 96, 32, Inet4Address.class);
		this.destinationAddress = (String) BinaryFunctions.bitsArrayToObject(bitsArray, 128, 32, Inet4Address.class);
		this.data = (String) bitsArray.substring(160, bitsArray.length());
	}
	
	public String packetToBitArray() {
		String bitsArray = 
				BinaryFunctions.toBitsArray(this.version, 4) + 
				BinaryFunctions.toBitsArray(this.headerLength, 4) + 
				BinaryFunctions.toBitsArray(this.seviceType, 8) + 
				BinaryFunctions.toBitsArray(this.totalLength, 16) + 
				BinaryFunctions.toBitsArray(this.identification, 16) + 
				BinaryFunctions.toBitsArray(this.reserved, 1) + 
				BinaryFunctions.toBitsArray(this.df, 1) + 
				BinaryFunctions.toBitsArray(this.mf, 1) + 
				BinaryFunctions.toBitsArray(this.fragmentOffset, 13) + 
				BinaryFunctions.toBitsArray(this.timeToLive, 8) + 
				BinaryFunctions.toBitsArray(this.protocol, 8) + 
				BinaryFunctions.toBitsArray(this.headerChecksum, 16) + 
				BinaryFunctions.toBitsArray(this.sourceAddress, 32) + 
				BinaryFunctions.toBitsArray(this.destinationAddress, 32) +
				this.data; 
		
		return bitsArray;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public boolean isDf() {
		return df;
	}

	public boolean isMf() {
		return mf;
	}

	public int getTimeToLive() {
		return timeToLive;
	}
	
	public int getHeaderChecksum() {
		return headerChecksum;
	}
}
