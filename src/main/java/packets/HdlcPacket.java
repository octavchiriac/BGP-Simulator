package packets;

public class HdlcPacket extends Packet{

	private String flag;
	private String address;
	private String control;
	private String fcs;

	public HdlcPacket(String flag, String address, String control, String data, String fcs) {
		super();
		this.flag = flag;
		this.address = address;
		this.control = control;
		this.data = data;
		this.fcs = fcs;
		super.id = Math.abs((int) System.currentTimeMillis());
	}

	public HdlcPacket(String bitsArray) {
		super();
		this.flag = bitsArray.substring(0, 8);
		this.address = bitsArray.substring(8, 16);
		this.control = bitsArray.substring(16, 24);
		this.data = bitsArray.substring(24, bitsArray.length() - 16);
		this.fcs = bitsArray.substring(bitsArray.length() - 16, bitsArray.length() - 8);
	}

	public String packetToBitArray() {
		String bitsArray =
				this.flag +
				this.address +
				this.control +
				this.data +
				this.fcs +
				this.flag;

		return bitsArray;
	}
}
