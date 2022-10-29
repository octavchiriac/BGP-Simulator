package components.tblentries;

public interface IAttributeBuilder {

    String ORIGIN = null;
    String AS_PATH = null;
    String NEXT_HOP = null;
    String MULTI_EXIT_DISC = null;
    String LOCAL_PREF = null;
    String ATOMIC_AGGREGATE = null;
    String AGGREGATOR = null;

    String PathAttribute();
}
