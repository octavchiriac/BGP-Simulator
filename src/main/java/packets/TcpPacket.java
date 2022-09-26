package packets;

public class TcpPacket {

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
    private String data;
	
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
		this.sourcePort = (int) bitsArrayToObject(bitsArray, 0, 16, Integer.class);
		this.destinationPort = (int) bitsArrayToObject(bitsArray, 16, 16, Integer.class);
		this.sequenceNumber = (int) bitsArrayToObject(bitsArray, 32, 32, Integer.class);
		this.acknowledgementNumber = (int) bitsArrayToObject(bitsArray, 64, 32, Integer.class);
		this.dataOffset = (int) bitsArrayToObject(bitsArray, 96, 4, Integer.class);
		this.reserved = (int) bitsArrayToObject(bitsArray, 100, 6, Integer.class);
		this.urg = (boolean) bitsArrayToObject(bitsArray, 106, 1, Boolean.class);
		this.ack = (boolean) bitsArrayToObject(bitsArray, 107, 1, Boolean.class);
		this.psh = (boolean) bitsArrayToObject(bitsArray, 108, 1, Boolean.class);
		this.rst = (boolean) bitsArrayToObject(bitsArray, 109, 1, Boolean.class);
		this.syn = (boolean) bitsArrayToObject(bitsArray, 110, 1, Boolean.class);
		this.fin = (boolean) bitsArrayToObject(bitsArray, 111, 1, Boolean.class);
		this.windowSize = (int) bitsArrayToObject(bitsArray, 112, 16, Integer.class);
		this.checksum = (int) bitsArrayToObject(bitsArray, 128, 16, Integer.class);
		this.urgentPointer = (int) bitsArrayToObject(bitsArray, 144, 16, Integer.class);
		this.data = (String) bitsArrayToObject(bitsArray, 160, -1, String.class);
	}
	
	public String packetToBitArray() {
		String bitsArray = 
				toBitsArray(this.sourcePort, 16) + 
				toBitsArray(this.destinationPort, 16) + 
				toBitsArray(this.sequenceNumber, 32) + 
				toBitsArray(this.acknowledgementNumber, 32) + 
				toBitsArray(this.dataOffset, 4) + 
				toBitsArray(this.reserved, 6) + 
				toBitsArray(this.urg, 1) + 
				toBitsArray(this.ack, 1) + 
				toBitsArray(this.psh, 1) + 
				toBitsArray(this.rst, 1) + 
				toBitsArray(this.syn, 1) + 
				toBitsArray(this.fin, 1) + 
				toBitsArray(this.windowSize, 16) + 
				toBitsArray(this.checksum, 16) + 
				toBitsArray(this.urgentPointer, 16) + 
				toBitsArray(this.data, 0); 
		
		return bitsArray;
	}
	
	public Object bitsArrayToObject(String bitsArray, int offset, int size, Class<?> type) {
		Object obj = null;
		String subst = "";
		
		if(size > -1) {
			subst = bitsArray.substring(offset, offset + size);
		} else {
			subst = bitsArray.substring(offset, bitsArray.length());
		}
		
		if(type.toString().contains("Integer")) {
			obj = Integer.parseInt(subst, 2);
		} else if(type.toString().contains("Boolean")) {
			obj = subst.equals("1") ? true : false;
		} else {
			obj = convertBinaryToString(subst);
		}
		return obj;
	}
	
	private static String toBitsArray (Object input, int size) {
		String bitsArray = "";
		
		if(input instanceof Boolean) {
			bitsArray += (boolean) input ? "1" : "0";
		} else if (input instanceof Integer) {
			bitsArray += String.format("%" + size + "s", Integer.toBinaryString((int) input))
					.replace(' ','0');
		} else {
			bitsArray += convertStringToBinary((String) input);
		}
		
		return bitsArray;
	}
	
	private static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();
    }
	
	private static String convertBinaryToString(String input) {
		StringBuilder stringBuilder = new StringBuilder();
	    int charCode;
	    
	    for (int i = 0; i < input.length(); i += 8) {
	        charCode = Integer.parseInt(input.substring(i, i + 8), 2);
	        String returnChar = Character.toString((char) charCode);
	        stringBuilder.append(returnChar);
	    }

	    return stringBuilder.toString();
	}

	public String getData() {
		return data;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public boolean isAck() {
		return ack;
	}

	public boolean isSyn() {
		return syn;
	}
}
