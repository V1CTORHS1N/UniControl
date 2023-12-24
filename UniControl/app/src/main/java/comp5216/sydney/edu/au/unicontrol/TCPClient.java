package comp5216.sydney.edu.au.unicontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TCPClient(String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendMessage() {
        try {
            this.out.println("Hi");
            String res = this.in.readLine();
            if ("Hi".equals(res)) {
                return clientSocket.getInetAddress().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendCommand(String data) {
        try {
            String out = RSAEncrypt.encrypt(data);
            this.out.println(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.in.close();
            this.out.close();
            this.clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getPublicKey() throws IOException {
        String rec = this.in.readLine();
        if (rec.equals("PUBLICKEY")) {
            this.out.println("READY");
            rec = this.in.readLine();
            RSAEncrypt.setPubKey(rec);
            return true;
        }
        return false;
    }
}
