package components;

import multithread.ReceiveTcpPacket;
import multithread.SendTcpPacket;
import multithread.ThreadPool;
import utils.TypeHandlers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static components.Globals.routers;
import static runner.DoTest.establishTcpConnection;

public class Router implements Runnable {

    private String name;
    private ArrayList<RouterInterface> interfaces;
    private boolean isEnabled;
    private boolean isRestarted;
    private RoutingTableEntry routingTable;
    private BlockingQueue<String> queue ;
    private NeighborTable neighborTable;
    private ArrayList<Router> tcpConnectedRouters;

    public Router(String name) {
        super();
        this.name = name;
        this.isEnabled = false;
        this.isRestarted = false;
        this.neighborTable = new NeighborTable();
        this.queue = new LinkedBlockingQueue<>();
        this.tcpConnectedRouters = new ArrayList<>();
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
        }
        System.out.println("[" + name  + "] Router state : " + (isEnabled? "Enabled" : "Disabled"));
    }

    private void restartRouter() {
        this.isRestarted = true;
        this.neighborTable = new NeighborTable();
        this.queue = new LinkedBlockingQueue<>();
        this.tcpConnectedRouters = new ArrayList<>();
    }

    public RoutingTableEntry getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(RoutingTableEntry routingTable) {
        this.routingTable = routingTable;
    }
    
    public NeighborTable getNeighborTable() {
		return neighborTable;
	}

    public ArrayList<Router> getTcpConnectedRouters() {
        return tcpConnectedRouters;
    }

    public void printTcpConnectedRouters() {
        System.out.print("[" + name  + "] TCP Connected routers : { ");
        for(Router r : tcpConnectedRouters) {
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
        for (Router r : routers) {{
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
            if (i.isUp) {
                enabledInterfaces.add(i);
            }
        }
        return enabledInterfaces;
    }
    
    public ArrayList<String> getEnabledInterfacesAddresses() {
        ArrayList<String> enabledInterfaces = new ArrayList<String>();
        for (RouterInterface i : interfaces) {
            if (i.isUp) {
                enabledInterfaces.add(i.getIpAddress());
            }
        }
        return enabledInterfaces;
    }

    public void queuePacket(String bs) {
        if(this.isEnabled) {
            System.out.println("\033[0;32m" + "[INFO] Packet queued at " + name + " at " +
                    TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()) + "\033[0m");
            queue.add(bs);
        } else {
            System.err.println("[ERROR] Router " + name + " unreachable. Packet dropped at " +
                    TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
        }
    }
   
    public void populateNeighborTable () {
		for(RouterInterface i : this.getInterfaces()) {
			
			if(i.getDirectLink() != null) {
				RouterInterface neighborRouter = getRouterInterfaceByIP(i.getDirectLink());
				
				neighborTable.addNeighbor(neighborRouter.getIpAddress(), neighborRouter.getAs());
				
				System.out.println("[" + name  + "] Discovered neighbor " + neighborRouter.getIpAddress() + 
						" from AS " + neighborRouter.getAs());
			}
		}
		
		Globals.nrRoutersStarted++;
    }

    public void printRouterInfo() {
        System.out.println(this.getName());
        for (RouterInterface inte : this.getInterfaces()) {
            System.out.println("Interface Name: " + inte.getName());
            System.out.println("IpAddress: " + inte.getIpAddress());
            System.out.println("Mask: " + inte.getSubnetMask());
            System.out.println("AS: " + inte.getAs());
            System.out.println("Direct link: " + inte.getDirectLink());
            System.out.println("\n");
        }
        System.out.println("########################");
    }
    
    public void printNeighborTable() {
    	System.out.println("[" + this.getName() + "] Neighbor table: \n" + this.getNeighborTable());
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("[" + name  + "] Router starting");
        
        // Enabling router
        this.isEnabled = true;
        
        System.out.println("[" + name  + "] Router state : enabled");
        
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
                System.err.println(e.getMessage());
            }
        }
    }
}