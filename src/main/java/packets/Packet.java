package packets;

public abstract class Packet {
	
	protected String data;
	
	abstract String packetToBitArray();
	
	public String getData() {
		return data;
	}

}
