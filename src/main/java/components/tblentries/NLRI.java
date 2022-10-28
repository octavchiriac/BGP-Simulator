package components.tblentries;

import java.util.ArrayList;

public class NLRI {
    private String Prefix;
    private int PrefixLength;

    public NLRI(String Prefix, int PrefixLength) {
        this.Prefix = Prefix;
        this.PrefixLength = PrefixLength;
    }

    public ArrayList<String> getNLRI() {
        ArrayList<String> NLRI = new ArrayList<>();
        NLRI.add(Prefix);
        NLRI.add(String.valueOf(PrefixLength));
        return NLRI;
    }
}
