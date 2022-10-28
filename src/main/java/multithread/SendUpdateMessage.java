package multithread;

/*
    Major fields of the BGP update message are as follows:

        - Unfeasible routes length—The total length of the withdrawn routes field in bytes.
            A value of 0 indicates no route is withdrawn from service, nor is the withdrawn routes field present in this update message.
        - Withdrawn routes—This is a variable length field that contains a list of withdrawn IP prefixes.
        - Total path attribute length—Total length of the path attributes field in bytes.
            A value of 0 indicates that no NLRI field is present in this update message.
        - Path attributes—List of path attributes related to NLRI.
            Each path attribute is a triple <attribute type, attribute length, attribute value> of variable length.
            BGP uses these attributes to avoid routing loops, and perform routing and protocol extensions.
        - NLRI—Each feasible route is represented as <length, prefix>.
 */

public class SendUpdateMessage implements Runnable {
    private long WithdrawnRoutesLength;
    private String WithdrawnRoutes;
    private String TotalPathAttributeLength;
    private String PathAttributes;
    private String NetworkLayerReachabilityInformation;

    public SendUpdateMessage(long WithdrawnRoutesLength, String WithdrawnRoutes, String TotalPathAttributeLength,
                             String PathAttributes, String NetworkLayerReachabilityInformation) {
        this.WithdrawnRoutesLength = WithdrawnRoutesLength;
        this.WithdrawnRoutes = WithdrawnRoutes;
        this.TotalPathAttributeLength = TotalPathAttributeLength;
        this.PathAttributes = PathAttributes;
        this.NetworkLayerReachabilityInformation = NetworkLayerReachabilityInformation;
    }

    @Override
    public void run() {
        System.out.println("Sending Update Message");
    }
}