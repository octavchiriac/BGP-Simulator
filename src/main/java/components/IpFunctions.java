package components;

public class IpFunctions {

    public static String getIpInBinary(String ip) {

        String binaryIp = "";
        String[] tokens = ip.split("\\.");

        for (String token : tokens) {
            String binaryToken = String.format("%8s", Integer.toBinaryString(Integer.parseInt(token))).replace(' ',
                    '0');
            binaryIp += binaryToken + ".";
        }

        return binaryIp.substring(0, binaryIp.length() - 1);
    }

    public static String getNetworkAddress(String ip, String mask) {

        String networkAddress = "";
        String[] tokensIp = ip.split("\\.");
        String[] tokensMask = mask.split("\\.");

        for (int i = 0; i < tokensIp.length; i++) {
            networkAddress += (Integer.parseInt(tokensIp[i]) & Integer.parseInt(tokensMask[i])) + ".";
        }

        return networkAddress.substring(0, networkAddress.length() - 1);
    }

    // TODO: Implement send-receive packet functionality
    public static void sendIPPacket(String sourceIp, String destinationIp, TCPpacket data) {
        System.out.println("Sending IP packet from " + sourceIp + " to " + destinationIp + " with data: " + data.toString());
    }
    public static void listenToPort(int port) {
        System.out.println("Listening to port " + port);
    }

}
