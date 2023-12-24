package comp5216.sydney.edu.au.unicontrol;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button connect, mouse, gamepad;
    private EditText _ip, _port;
    private String address = "", port;
    protected TCPClient tcpClient;
    protected UDPClient udpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] PERMISSIONS = new String[] {
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
        }

        this.connect = (Button) findViewById(R.id.connect);
        this.mouse = (Button) findViewById(R.id.mouse);
        this.gamepad = (Button) findViewById(R.id.gamepad);

        this._ip = (EditText) findViewById(R.id.ip);
        this._port = (EditText) findViewById(R.id.port);

        this.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tcpClient = new TCPClient(_ip.getText().toString(), 5216);
                        boolean connecting = true;
                        while (connecting) {
                            try {
                                if (!tcpClient.sendMessage().equals("")) {
                                    connecting = false;
                                    address = _ip.getText().toString();
                                    port = _port.getText().toString();
                                    while (!tcpClient.getPublicKey());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            enableButtons();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            udpClient = new UDPClient(address, Integer.parseInt(port) + 1);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        SocketHandler.setTcpClient(tcpClient);
                        SocketHandler.setUdpClient(udpClient);
                    }
                });
                thread.start();
            }
        });

        this.mouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MouseActivity.class);
                startActivity(intent);
            }
        });
        this.gamepad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GamepadActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.tcpClient.stop();
        this.udpClient.close();
    }

    private void enableButtons() {
        this.mouse.setEnabled(true);
        this.gamepad.setEnabled(true);
    }
}