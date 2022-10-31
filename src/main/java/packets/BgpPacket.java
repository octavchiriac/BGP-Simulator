package packets;

import utils.BinaryFunctions;

public class BgpPacket extends Packet{

	private int version;
    private int as;
    private int holdTime;
    private long id;

	public BgpPacket(int version, int as, int holdTime, long id) {
		this.version = version;
		this.as = as;
		this.holdTime = holdTime;
		this.id = id;
	}

	public BgpPacket(String bitsArray) {
		super();
		this.version = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 0, 8, Integer.class);
		this.as = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 8, 16, Integer.class);
		this.holdTime = (int) BinaryFunctions.bitsArrayToObject(bitsArray, 24, 8, Integer.class);
		this.id = (long) BinaryFunctions.bitsArrayToObject(bitsArray, 32, 24, Long.class);
	}
	
	public String packetToBitArray() {
		String bitsArray = 
				BinaryFunctions.toBitsArray(this.version, 8) +
				BinaryFunctions.toBitsArray(this.as, 16) +
				BinaryFunctions.toBitsArray(this.holdTime, 8) +
				BinaryFunctions.toBitsArray(this.id, 24);

		return bitsArray;
	}

	public int getVersion() {
		return version;
	}

	public int getAs() {
		return as;
	}

	public int getHoldTime() {
		return holdTime;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "BgpPacket{" +
				"version=" + version +
				", as=" + as +
				", holdTime=" + holdTime +
				", id=" + id +
				'}';
	}
}
