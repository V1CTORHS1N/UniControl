package comp5216.sydney.edu.au.unicontrol;

public class SocketHandler {

    private static TCPClient tcpClient;
    private static UDPClient udpClient;

    public static synchronized void setUdpClient(UDPClient udpClient) {
        SocketHandler.udpClient = udpClient;
    }

    public static synchronized void setTcpClient(TCPClient tcpClient) {
        SocketHandler.tcpClient = tcpClient;
    }

    public static synchronized UDPClient getUdpClient() {
        return SocketHandler.udpClient;
    }

    public static synchronized TCPClient getTcpClient() {
        return SocketHandler.tcpClient;
    }

}
