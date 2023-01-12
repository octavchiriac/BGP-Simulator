package components.tblentries;

public class NeighborTableEntry {
    String ip;
    String as;
    double directTrust;

    public NeighborTableEntry(String ip, String as, double trust) {
        this.ip = ip;
        this.as = as;
        this.directTrust = trust;
    }

    public String getIp() {
        return ip;
    }

    public String getAs() {
        return as;
    }

    public double getTrust() {
        return directTrust;
    }
}
