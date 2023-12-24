package comp5216.sydney.edu.au.unicontrol;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buf;

    public UDPClient(String address, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public void sendPacket(String data) throws Exception {
        String out = RSAEncrypt.encrypt(data);
        this.buf = out.getBytes();
        DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, this.address, this.port);
        this.socket.send(packet);
    }

    public void close() {
        this.socket.close();
    }
}
