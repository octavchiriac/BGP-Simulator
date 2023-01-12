package components;

import components.tblentries.NeighborTableEntry;
import components.tblentries.PathAttributes;
import components.tblentries.PathSegments;
import de.vandermeer.asciitable.AsciiTable;
import multithread.ReceiveTcpPacket;
import multithread.ThreadPool;
import utils.TypeHandlers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static components.Globals.routers;

public class Router implements Runnable {

    private final long id;
    private String name;
    private ArrayList<RouterInterface> interfaces;
    private boolean isEnabled;
    private boolean isRestarted;
    private TopologyTable topologyTable;
    public BGPRoutingTable routingTable;
    private BlockingQueue<String> queue;
    private NeighborTable neighborTable;

    private ArrayList<Router> tcpConnectedRouters;

    public Router(String name) {
        super();
        this.id = (long) Math.abs(System.currentTimeMillis() % Math.pow(10, 10));
        this.name = name;
        this.isEnabled = false;
        this.isRestarted = false;
        this.neighborTable = new NeighborTable();
        this.queue = new LinkedBlockingQueue<>();
        this.tcpConnectedRouters = new ArrayList<>();
        this.routingTable = new BGPRoutingTable();
        this.topologyTable = new TopologyTable();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RouterInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(ArrayList<RouterInterface> interfaces2) {
        this.interfaces = interfaces2;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isRestarted() {
        return isRestarted;
    }

    public void setEnabled(boolean isEnabled) throws InterruptedException {
        this.isEnabled = isEnabled;

        if (isEnabled) {
            restartRouter();
        } else {
            // Change BGP states to Active after router is disabled
            for (RouterInterface inter : this.getInterfaces()) {
                inter.setState(BGPStates.Active);
                System.out.println("\033[0;35m" + "[" + name + " - " + inter.getName() + "] BGP state : Active" + "\033[0m");
            }
        }
        System.out.println("[" + name + "] Router state : " + (isEnabled ? "Enabled" : "Disabled"));
    }

    private void restartRouter() {

        // Change BGP states to Idle after router is restarted
        for (RouterInterface inter : this.getInterfaces()) {
            inter.setState(BGPStates.Idle);
            System.out.println("\033[0;35m" + "[" + name + " - " + inter.getName() + "] BGP state : Idle" + "\033[0m");
        }

        this.isRestarted = true;
        this.neighborTable = new NeighborTable();
        this.queue = new LinkedBlockingQueue<>();
        this.tcpConnectedRouters = new ArrayList<>();
    }

    public NeighborTable getNeighborTable() {
        return neighborTable;
    }

    public boolean updateBGPRoutingTable() {
        return routingTable.updateTable(topologyTable);
    }

    public void printRoutingTable() {
        synchronized (Globals.lock) {
            System.out.println("[" + name + "] BGP Routing table: ");
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRule();
            at.addRow("ID", "Destination IP", "AS_PATH", "NEXT_HOP", "MULTI_EXIT_DISC", "LOCAL_PREF");
            at.addRule();
            at.addRule();
            int i = 1;
            for (Map.Entry<String, PathAttributes> entry : routingTable.getBestRoutes().entrySet()) {

                // Just printing the AS_PATH instead of the IP addresses to make it easier to read
                PathSegments[] ps = entry.getValue().getAS_PATH();
                ArrayList<String> asPaths = new ArrayList<String>();
                for (PathSegments p : ps) {
                    for (String ip : p.getPathSegmentValue()) {
                        Router r = getRouterByIP(ip);
                        assert r != null;
                        r.getInterfaces().forEach(routerInterface -> {
                            if (routerInterface.getIpAddress().equals(ip)) {
                                if (!asPaths.contains(routerInterface.getAs())) {
                                    asPaths.add(routerInterface.getAs());
                                }
                            }
                        });
                    }
                }

                // Adding each row to the table
                at.addRow(i, entry.getKey(), asPaths.toString(), entry.getValue().getNEXT_HOP(), entry.getValue().getMULTI_EXIT_DISC(), entry.getValue().getLOCAL_PREF());
                at.addRule();
                i++;
            }
            System.out.println(at.render());
        }
    }

    public void printTopologyTable() {
        synchronized (Globals.lock) {
            System.out.println("[" + name + "] LNRI Topology table: ");
            AsciiTable at = new AsciiTable();
            at.addRule();
            at.addRule();
            at.addRow("Destination IP", "Path Attributes");
            at.addRule();
            at.addRule();
            for (Map.Entry<String, PathAttributes> entry : topologyTable.getTopTable().entrySet()) {
                at.addRow(entry.getKey(), entry.getValue().toString());
                at.addRule();
            }
            System.out.println(at.render());
        }
    }

    public TopologyTable getTopologyTable() {
        return topologyTable;
    }

    public ArrayList<Router> getTcpConnectedRouters() {
        return tcpConnectedRouters;
    }

    public void printTcpConnectedRouters() {
        System.out.print("[" + name + "] TCP Connected routers : { ");
        for (Router r : tcpConnectedRouters) {
            System.out.print(r.getName() + " ");
        }
        System.out.println("}");
    }

    public void addTcpConnectedRouter(Router r) {
        this.tcpConnectedRouters.add(r);
    }

    public static RouterInterface getRouterInterfaceByIP(String ip) {
        for (Router r : routers) {
            for (RouterInterface i : r.getEnabledInterfaces()) {
                if (i.getIpAddress().equals(ip)) {
                    return i;
                }
            }
        }
        return null;
    }

    public static Router getRouterByIP(String ip) {
        for (Router r : routers) {
            for (RouterInterface i : r.getEnabledInterfaces()) {
                if (i.getIpAddress().equals(ip)) {
                    return r;
                }
            }
        }
        return null;
    }

    public static Router getRouterByName(String name) {
        for (Router r : routers) {
            {
                if (r.getName().equals(name)) {
                    return r;
                }
            }
        }
        return null;
    }

    public ArrayList<RouterInterface> getEnabledInterfaces() {
        ArrayList<RouterInterface> enabledInterfaces = new ArrayList<RouterInterface>();
        for (RouterInterface i : interfaces) {
            if (i.isUp()) {
                enabledInterfaces.add(i);
            }
        }
        return enabledInterfaces;
    }

    public ArrayList<String> getEnabledInterfacesAddresses() {
        ArrayList<String> enabledInterfaces = new ArrayList<String>();
        for (RouterInterface i : interfaces) {
            if (i.isUp()) {
                enabledInterfaces.add(i.getIpAddress());
            }
        }
        return enabledInterfaces;
    }

    public void queuePacket(String bs) {
        if (this.isEnabled) {
            System.out.println("\033[0;32m" + "[INFO] Packet queued at " + name + " at " +
                    TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()) + "\033[0m");
            queue.add(bs);
        } else {
            System.err.println("[ERROR] Router " + name + " unreachable. Packet dropped at " +
                    TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
        }
    }

    public void populateNeighborTable() {
        for (RouterInterface i : this.getInterfaces()) {

            // Change BGP state to Connect
            i.setState(BGPStates.Connect);
            System.out.println("\033[0;35m" + "[" + name + " - " + i.getName() + "] BGP state : Connect" + "\033[0m");

            if (i.getDirectLink() != null) {
                RouterInterface neighborRouter = getRouterInterfaceByIP(i.getDirectLink());

                double directTrust = Math.random();
                DecimalFormat df = new DecimalFormat("#.##");
                directTrust = Double.parseDouble(df.format(directTrust));

                neighborTable.addNeighbor(neighborRouter.getIpAddress(), neighborRouter.getAs(), directTrust);

                System.out.println("[" + name + "] Discovered neighbor " + neighborRouter.getIpAddress() +
                        " from AS " + neighborRouter.getAs() + " with direct trust " + directTrust);
            }
        }

        Globals.nrRoutersStarted++;
    }

    public void printRouterInfo() {
        System.out.println(this.getName() + " " + this.getId());

        for (RouterInterface inter : this.getInterfaces()) {
            System.out.println("Interface Name: " + inter.getName());
            System.out.println("IpAddress: " + inter.getIpAddress());
            System.out.println("Mask: " + inter.getSubnetMask());
            System.out.println("AS: " + inter.getAs());
            System.out.println("Direct link: " + inter.getDirectLink());
            System.out.println("\n");
        }
        System.out.println("########################");
    }

    public void printNeighborTable() {
        synchronized (Globals.lock) {
            System.out.println("[" + this.getName() + "] Neighbor table:");
            for (NeighborTableEntry entry : this.getNeighborTable().getNeighborInfo()) {
                System.out.println("IP: " + entry.getIp() + " AS: " + entry.getAs() + " DirectTrust: " + entry.getTrust());
            }
        }
    }

    @Override
    public void run() {
        System.out.println("[" + name + "] Router starting");

        // Enabling router
        this.isEnabled = true;

        System.out.println("[" + name + "] Router state : Enabled");

        // Adding direct links to the neighbor table
        populateNeighborTable();

        // Listening for incoming messages
        while (isEnabled) {
            try {
                String msg;
                while ((msg = queue.poll()) != null) {

                    // Creating a task to handle the packet and adding it to the thread pool
                    ReceiveTcpPacket task = new ReceiveTcpPacket(msg);
                    ThreadPool.submit(task);
                }
            } catch (Exception e) {
                System.err.println("Error in handling TCP packet at " + this.getName() + ": " + e.getMessage());
            }
        }
    }
}