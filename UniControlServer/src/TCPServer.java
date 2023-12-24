import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class TCPServer extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Executor executor;

    public TCPServer() {
        try {
            this.executor = new Executor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startConnection(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.clientSocket = serverSocket.accept();
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String rec = in.readLine();
            if ("Hi".equals(rec)) {
                this.out.println("Hi");
                this.out.println("PUBLICKEY");
                if (this.in.readLine().equals("READY")) {
                    this.out.println(RSA.getPublicKey());
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            String rec = null;
            try {
                rec = this.in.readLine();
                rec = RSA.decrypt(rec);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (rec != null)
                this.executor.execute(rec);
        }
    }
}
