package multithread;

public class SendTrustExchangeMessage implements Runnable {

    private String source;
    private String destination;
    private double totalTrust;

    public SendTrustExchangeMessage(String source, String destination, double totalTrust) {
        this.source = source;
        this.destination = destination;
        this.totalTrust = totalTrust;
    }

    @Override
    public void run() {

    }
}
