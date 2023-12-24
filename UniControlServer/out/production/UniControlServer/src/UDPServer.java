import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends Thread{
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private final Executor executor;

    public UDPServer() throws Exception {
        this.socket = new DatagramSocket(5217);
        this.executor = new Executor();
    }

    public void run() {
        while(true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                String decrypted = RSA.decrypt(new String(data, 0, packet.getLength()));
                this.executor.execute(decrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
