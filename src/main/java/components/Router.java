package components;

import multithread.HandlePktTask;
import multithread.ThreadPool;
import packets.Packet;
import utils.TypeHandlers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Router implements Runnable {

    public String name;
    public ArrayList<RouterInterface> interfaces;
    public boolean isEnabled;
    public RoutingTableEntry routingTable;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public Router(String name) {
        super();
        this.name = name;
        this.isEnabled = true;
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

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public RoutingTableEntry getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(RoutingTableEntry routingTable) {
        this.routingTable = routingTable;
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

    public void queuePacket(String bs, int id) {
        System.out.println("Packet queued at " + name + " through link " + id + " at " + TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
        queue.add(bs);
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

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("\n");
        System.out.println("Router " + name + " starting, is the router up: " + isEnabled);
        System.out.println("\n");
        System.out.println("########################");
        while (isEnabled) {
            try {
                String msg;
                while ((msg = queue.poll()) != null) {
                    System.out.println("Packet received at " + name + " at " + TypeHandlers.getCurrentTimeFromMillis(System.currentTimeMillis()));
                    // Creating a task to handle the packet and adding it to the thread pool
                    HandlePktTask task = new HandlePktTask(msg, name);
                    ThreadPool.submit(task);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
