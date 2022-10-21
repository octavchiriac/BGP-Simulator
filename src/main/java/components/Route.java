package components;

/*
    * Route class to indicate the path of a route, used by RIB, therefore there is no out interface
 */
public class Route {

    public String destinationIP; //Destination IP address of the route.
    public String nextHop; //Next hop IP address.

    public Route(String destinationIP, String nextHop) {
        super();
        this.destinationIP = destinationIP;
        this.nextHop = nextHop;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    @Override
    public String toString() {
        return "Route{" + "destinationIP=" + destinationIP + ", nextHop=" + nextHop + '}';
    }
}
