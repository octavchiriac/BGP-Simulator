package multithread;

import components.BGPStates;
import components.Globals;
import components.Router;
import components.RouterInterface;

public class SendKeepAliveMessage implements Runnable{

    String source;
    String destination;

    public SendKeepAliveMessage(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void run() {
        Router r1 = Router.getRouterByIP(this.source);
        RouterInterface i1 = r1.getRouterInterfaceByIP(this.source);

        Router r2 = Router.getRouterByIP(this.destination);
        RouterInterface i2 = r2.getRouterInterfaceByIP(this.destination);

        int seqNumber = 0;

        try {
                while (r1.getTcpConnectedRouters().contains(r2)) {
                    if (r1.isEnabled() &&
                            (i1.getState().equals(BGPStates.OpenConfirm) ||
                                    i1.getState().equals(BGPStates.Established))) {
                        SendTcpPacket task1 = new SendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, seqNumber, 0,
                                this.source, this.destination, Globals.DESTINATION_MAC_ADDRESS,
                                false, true, true, false, "");
                        ThreadPool.submit(task1);
                    }

                    if (r2.isEnabled() &&
                            (i2.getState().equals(BGPStates.OpenConfirm) ||
                                    i2.getState().equals(BGPStates.Established))) {
                        SendTcpPacket task2 = new SendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, seqNumber, 0,
                                this.destination, this.source, Globals.DESTINATION_MAC_ADDRESS,
                                false, true, true, false, "");
                        ThreadPool.submit(task2);
                    }

                    seqNumber += 1;

                    Thread.sleep(Globals.HOLD_TIMER);
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
    }
}
