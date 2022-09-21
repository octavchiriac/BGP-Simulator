package components;

public class TCPconn {

    private String sourceIp;
    private String destinationIp;
    private int sourcePort;
    private int destinationPort;
    private String state;
    private String sequenceNumber;
    private String ackNumber;

    public TCPconn(String sourceIp, String destinationIp, int sourcePort, int destinationPort, String state) {
        super();
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.state = state;
        this.sequenceNumber = String.format("%32s", Integer.toBinaryString(1)).replace(' ', '0');
        this.ackNumber = String.format("%32s", Integer.toBinaryString(0)).replace(' ', '0');
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAckNumber() {
        return ackNumber;
    }

    public void setAckNumber(String ackNumber) {
        this.ackNumber = ackNumber;
    }

    public void sendPkt(String seqNumBinary, String ackNumBinary, boolean syn, boolean ack, boolean fin, String data) {
        String srcIpBinary = IpFunctions.getIpInBinary(this.sourceIp);
        String dstIpBinary = IpFunctions.getIpInBinary(this.destinationIp);

        TCPpacket ackPacket = new TCPpacket(srcIpBinary, dstIpBinary, seqNumBinary, ackNumBinary,
                "24", false, ack, false, syn, fin, "32768", data);

        System.out.println("Sending packet: " + ackPacket.toString());
        IpFunctions.sendIPPacket(this.sourceIp, this.destinationIp, ackPacket);
    }

    public void printTCPconnInfo() {
        System.out.println("Source IP: " + this.getSourceIp());
        System.out.println("Destination IP: " + this.getDestinationIp());
        System.out.println("Source Port: " + this.getSourcePort());
        System.out.println("Destination Port: " + this.getDestinationPort());
        System.out.println("State: " + this.getState());
        System.out.println("\n########################");
    }

}
