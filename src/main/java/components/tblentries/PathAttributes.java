package components.tblentries;

public class PathAttributes {

    private  String ORIGIN;
    private String AS_PATH;
    private String NEXT_HOP;
    private String MULTI_EXIT_DISC;
    private String LOCAL_PREF;
    private String ATOMIC_AGGREGATE;
    private String AGGREGATOR;

    // TODO: OPTIONAL ATTRIBUTES
    public PathAttributes(String ORIGIN, String AS_PATH, String NEXT_HOP, String MULTI_EXIT_DISC, String LOCAL_PREF, String ATOMIC_AGGREGATE, String AGGREGATOR) {
        this.ORIGIN = ORIGIN;
        this.AS_PATH = AS_PATH;
        this.NEXT_HOP = NEXT_HOP;
        this.MULTI_EXIT_DISC = MULTI_EXIT_DISC;
        this.LOCAL_PREF = LOCAL_PREF;
        this.ATOMIC_AGGREGATE = ATOMIC_AGGREGATE;
        this.AGGREGATOR = AGGREGATOR;
    }

    public String getPathAttributes() {
        return ORIGIN + AS_PATH + NEXT_HOP + MULTI_EXIT_DISC + LOCAL_PREF + ATOMIC_AGGREGATE + AGGREGATOR;
    }

    public String getORIGIN() {
        return ORIGIN;
    }

    public String getAS_PATH() {
        return AS_PATH;
    }

    public String getNEXT_HOP() {
        return NEXT_HOP;
    }

    public String getMULTI_EXIT_DISC() {
        return MULTI_EXIT_DISC;
    }

    public String getLOCAL_PREF() {
        return LOCAL_PREF;
    }

    public String getATOMIC_AGGREGATE() {
        return ATOMIC_AGGREGATE;
    }

    public String getAGGREGATOR() {
        return AGGREGATOR;
    }
}