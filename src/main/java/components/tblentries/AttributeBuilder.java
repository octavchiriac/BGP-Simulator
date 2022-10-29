package components.tblentries;

/// <summary>
/// Concrete builder implementation
/// </summary>
public class AttributeBuilder {

    private String ORIGIN = "0";
    private String AS_PATH = "0";
    private String NEXT_HOP = "0";
    private String MULTI_EXIT_DISC = null;
    private String LOCAL_PREF = null;
    private String ATOMIC_AGGREGATE = null;
    private String AGGREGATOR = null;

    public AttributeBuilder() { }

    public PathAttributes AttributeBuilder() {
        return new PathAttributes(ORIGIN, AS_PATH, NEXT_HOP, MULTI_EXIT_DISC, LOCAL_PREF, ATOMIC_AGGREGATE, AGGREGATOR);
    }

    public AttributeBuilder setORIGIN(String ORIGIN) {
        this.ORIGIN = ORIGIN;
        return this;
    }

    public AttributeBuilder setAS_PATH(String AS_PATH) {
        this.AS_PATH = AS_PATH;
        return this;
    }

    public AttributeBuilder setNEXT_HOP(String NEXT_HOP) {
        this.NEXT_HOP = NEXT_HOP;
        return this;
    }

    public AttributeBuilder setMULTI_EXIT_DISC(String MULTI_EXIT_DISC) {
        this.MULTI_EXIT_DISC = MULTI_EXIT_DISC;
        return this;
    }

    public AttributeBuilder setLOCAL_PREF(String LOCAL_PREF) {
        this.LOCAL_PREF = LOCAL_PREF;
        return this;
    }

    public AttributeBuilder setATOMIC_AGGREGATE(String ATOMIC_AGGREGATE) {
        this.ATOMIC_AGGREGATE = ATOMIC_AGGREGATE;
        return this;
    }

    public AttributeBuilder setAGGREGATOR(String AGGREGATOR) {
        this.AGGREGATOR = AGGREGATOR;
        return this;
    }
}
