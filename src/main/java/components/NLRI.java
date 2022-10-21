package components;

/*
The Network Layer Reachability Information (NLRI) is exchanged between BGP routers using UPDATE messages.
An NLRI is composed of a LENGTH and a PREFIX.
The length is a network mask in CIDR notation (eg. /25) specifying the number of network bits, and the prefix is the Network address for that subnet.

BGP refere the prefixes to NLRI basically NLRIs are the prefixes that can be reached through the advertising BGP neighbor
 */

public class NLRI {

    public String length; //Length of the route (in CIDR notation)
    public String prefix; //Prefix of the route.


    public NLRI(String length, String prefix) {
        super();
        this.length = length;
        this.prefix = prefix;
    }

    public String getNLRI(){
        return "{"+length + "; " + prefix+"}";
    }


}
