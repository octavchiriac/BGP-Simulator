package runner;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import multithread.SendPktTask;
import multithread.ThreadPool;
import components.Globals;
import components.Router;
import packets.HdlcPacket;
import packets.IpPacket;
import packets.Packet;
import packets.TcpPacket;
import utils.ParseInputFile;

public class DoTest {
	
	
	public static void establishTcpConection(Router sourceRouter, Router destinationRouter) {
		
		String bitArrayHdlc;
		boolean hasError;

		// Sending SYN packet from source router
		System.out.println("[R1 -> R2]");
		System.out.println("Sending SYN packet");
		bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 0, 0, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
			 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
			 Globals.DESTINATION_MAC_ADDRESS, true, false);
		
		// Receiving SYN packet to destination router
		hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, true, false);
		
		if(!hasError) {
			System.out.println("[R1 <- R2]");
			System.out.println("Sending SYN+ACK packet");
			bitArrayHdlc = destinationRouter.sendTcpPacket(Globals.TCP_PORT, Globals.UDP_PORT, 0, 1, destinationRouter.getEnabledInterfaces().get(2).getIpAddress(),
					 sourceRouter.getEnabledInterfaces().get(1).getIpAddress(), 
					 Globals.DESTINATION_MAC_ADDRESS, true, true);
				
			// Receiving SYN packet to destination router
			hasError = sourceRouter.receiveTcpPacket(bitArrayHdlc, true, true);
			
			if(!hasError) {
				System.out.println("[R1 -> R2]");
				System.out.println("Sending ACK packet");
				bitArrayHdlc = sourceRouter.sendTcpPacket(Globals.UDP_PORT, Globals.TCP_PORT, 1, 1, sourceRouter.getEnabledInterfaces().get(1).getIpAddress(),  //HARDCODED FOR NOW, WAITING FOR NEIGHBOR TABLE
						 destinationRouter.getEnabledInterfaces().get(2).getIpAddress(), 
						 Globals.DESTINATION_MAC_ADDRESS, false, true);
					
					// Receiving SYN packet to destination router
					hasError = destinationRouter.receiveTcpPacket(bitArrayHdlc, false, true);
					
					if(!hasError) {
						System.out.println("[R1 <-> R2]");
						System.out.println("Connection established!");
					}
			}
		}
	}

    public static void main(String[] args) throws IOException, InterruptedException {

        // TEST PARSING INPUT FILE
        ParseInputFile parseInput = new ParseInputFile();
        parseInput.parseRouterInterfaces();
        parseInput.parseDirectLinks();
//
//        for (int i = 0; i < 3; i++) {
//            Globals.routers.get(i).printRouterInfo();
//        }
//
        // ROUTERS NEED TO BE RUN AT STARTUP AFTER FILE PARSING
//        for (Router r : Globals.routers) {
//            Thread t = new Thread(r);
//            t.start();
//        }     

        Router r1 = Globals.routers.get(0);
        Router r2 = Globals.routers.get(1);
       
        establishTcpConection(r1, r2);

        
//
//        // Test thread pool
//        SendPktTask task1 = new SendPktTask(hdlcPacket2.packetToBitArray(), "100.1.2.3");
//
//        destinationAddress = "100.1.2.1";
//        tcpPacket = new TcpPacket(1027, 179, 7, 7, 7, 7, false, false, false, false, true, true, 0, 0, 0, "MUIE 123 MUMU23");
//        bitArrayTcp = tcpPacket.packetToBitArray();
//        ipPacket = new IpPacket(4, 5, 0, 15, 3, false, false, true, 0, 255, 6, 7, sourceAddress, destinationAddress, bitArrayTcp);
//        bitArrayIp = ipPacket.packetToBitArray();
//        hdlcPacket = new HdlcPacket("01111110", "11111111", "00000000", bitArrayIp, "00000000");
//        bitArrayHdlc = hdlcPacket.packetToBitArray();
//        hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
//
//        SendPktTask task2 = new SendPktTask(hdlcPacket2.packetToBitArray(), "100.1.2.1");
//
//        destinationAddress = "10.0.0.2";
//        tcpPacket = new TcpPacket(1027, 179, 7, 7, 7, 7, false, false, false, false, true, true, 0, 0, 0, "MUIE 123 MUMU23");
//        bitArrayTcp = tcpPacket.packetToBitArray();
//        ipPacket = new IpPacket(4, 5, 0, 15, 3, false, false, true, 0, 255, 6, 7, sourceAddress, destinationAddress, bitArrayTcp);
//        bitArrayIp = ipPacket.packetToBitArray();
//        hdlcPacket = new HdlcPacket("01111110", "11111111", "00000000", bitArrayIp, "00000000");
//        bitArrayHdlc = hdlcPacket.packetToBitArray();
//        hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
//
//        SendPktTask task3 = new SendPktTask(hdlcPacket2.packetToBitArray(), "10.0.0.2");
//
//        destinationAddress = "10.1.50.4";
//        tcpPacket = new TcpPacket(1027, 179, 7, 7, 7, 7, false, false, false, false, true, true, 0, 0, 0, "MUIE 123 MUMU23");
//        bitArrayTcp = tcpPacket.packetToBitArray();
//        ipPacket = new IpPacket(4, 5, 0, 15, 3, false, false, true, 0, 255, 6, 7, sourceAddress, destinationAddress, bitArrayTcp);
//        bitArrayIp = ipPacket.packetToBitArray();
//        hdlcPacket = new HdlcPacket("01111110", "11111111", "00000000", bitArrayIp, "00000000");
//        bitArrayHdlc = hdlcPacket.packetToBitArray();
//        hdlcPacket2 = new HdlcPacket(bitArrayHdlc);
//
//        SendPktTask task4 = new SendPktTask(hdlcPacket2.packetToBitArray(), "10.1.50.4");
//        ThreadPool.run();
//
//        ThreadPool.submit(task1);
//        ThreadPool.submit(task2);
//        ThreadPool.submit(task3);
//        ThreadPool.submit(task4);
    }
}
