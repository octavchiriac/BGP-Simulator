package components;

public class TCPpacket {

    private String sourcePort;
    private String destinationPort;
    private String sequenceNumber;
    private String acknowledgementNumber;
    private String dataOffset;
    private String reserved;
    private boolean ns;
    private boolean cwr;
    private boolean ece;
    private boolean urg;
    private boolean ack;
    private boolean psh;
    private boolean rst;
    private boolean syn;
    private boolean fin;
    private String windowSize;
    private String checksum;
    private String data;

    public TCPpacket(String sourcePort, String destinationPort,
                     String sequenceNumber, String acknowledgementNumber, String dataOffset, boolean ns,
                     boolean ack, boolean rst, boolean syn, boolean fin,
                     String windowSize, String data) {
        super();
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgementNumber = acknowledgementNumber;
        this.dataOffset = dataOffset;
        this.reserved = "000";
        this.ns = ns;
        this.cwr = false;
        this.ece = false;
        this.urg = false;
        this.ack = ack;
        this.psh = false;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        this.windowSize = windowSize;
        this.checksum = "correct";
        this.data = data;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public String getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(String dataOffset) {
        this.dataOffset = dataOffset;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public boolean isNs() {
        return ns;
    }

    public void setNs(boolean ns) {
        this.ns = ns;
    }

    public boolean isCwr() {
        return cwr;
    }

    public void setCwr(boolean cwr) {
        this.cwr = cwr;
    }

    public boolean isEce() {
        return ece;
    }

    public void setEce(boolean ece) {
        this.ece = ece;
    }

    public boolean isUrg() {
        return urg;
    }

    public void setUrg(boolean urg) {
        this.urg = urg;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isPsh() {
        return psh;
    }

    public void setPsh(boolean psh) {
        this.psh = psh;
    }

    public boolean isRst() {
        return rst;
    }

    public void setRst(boolean rst) {
        this.rst = rst;
    }

    public boolean isSyn() {
        return syn;
    }

    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TCPpacket{" +
                "sourcePort='" + sourcePort + '\'' +
                ", destinationPort='" + destinationPort + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", acknowledgementNumber='" + acknowledgementNumber + '\'' +
                ", dataOffset='" + dataOffset + '\'' +
                ", reserved='" + reserved + '\'' +
                ", ns=" + ns +
                ", cwr=" + cwr +
                ", ece=" + ece +
                ", urg=" + urg +
                ", ack=" + ack +
                ", psh=" + psh +
                ", rst=" + rst +
                ", syn=" + syn +
                ", fin=" + fin +
                ", windowSize='" + windowSize + '\'' +
                ", checksum='" + checksum + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
