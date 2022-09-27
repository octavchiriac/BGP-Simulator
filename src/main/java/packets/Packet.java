package packets;

public abstract class Packet {
	
	protected String data;
	
	public abstract String packetToBitArray();
	
	public String getData() {
		return data;
	}

}
