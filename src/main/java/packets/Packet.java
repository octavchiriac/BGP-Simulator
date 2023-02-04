package packets;

public abstract class Packet {
	
	protected String data;
	int id;
	public abstract String packetToBitArray();
	public String getData() {
		return data;
	}
	public long getId() {
		return id;
	}
}
