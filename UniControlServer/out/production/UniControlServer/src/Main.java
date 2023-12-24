import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("IP Address: " + address.getHostAddress());
            System.out.println("Waiting for connection...");
            RSA.genKeyPair();
            TCPServer TCPserver = new TCPServer();
            while (!TCPserver.startConnection(5216));
            System.out.println("Connection Established!");
            UDPServer UDPserver = new UDPServer();
            Thread UDPThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    UDPserver.run();
                }
            });
            Thread TCPThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TCPserver.run();
                }
            });
            UDPThread.start();
            TCPThread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}