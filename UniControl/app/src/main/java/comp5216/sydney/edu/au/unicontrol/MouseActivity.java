package comp5216.sydney.edu.au.unicontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class MouseActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelSensor;
    private Button left, right, scroll_up, scroll_down, input, calibrate, backspace, enter;
    private String[] TAG = {"MOVE", "LEFT_DOWN", "RIGHT_DOWN", "LEFT_UP", "RIGHT_UP", "SCROLL_UP_START", "SCROLL_DOWN_START", "CALIBRATE", "INPUT", "BACKSPACE", "ENTER", "SCROLL_UP_STOP", "SCROLL_DOWN_STOP"};

    private String accData = "", comData = "";
    private UDPClient UDPclient;
    private TCPClient TCPclient;
    private Thread UDPthread, TCPthread;
    private boolean running = false, requestCalibrate = false;

    public double AccX, AccY, cAccX, cAccY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        this.running = true;

        this.left = (Button) findViewById(R.id.left_click);
        this.right = (Button) findViewById(R.id.right_click);
        this.scroll_up = (Button) findViewById(R.id.scrollup);
        this.scroll_down = (Button) findViewById(R.id.scrolldown);
        this.input = (Button) findViewById(R.id.input);
        this.calibrate = (Button) findViewById(R.id.calibrate);
        this.backspace = (Button) findViewById(R.id.backspace);
        this.enter = (Button) findViewById(R.id.enter);

        this.left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = TAG[2];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = TAG[4];
                        break;
                    default:
                        key = "NONE";
                }
                comData = String.format("%s#", key);
                return false;
            }
        });
        this.right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = TAG[1];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = TAG[3];
                        break;
                    default:
                        key = "NONE";
                }
                comData = String.format("%s#", key);
                return false;
            }
        });
        this.scroll_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = TAG[5];
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = TAG[5];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = TAG[11];
                        break;
                    default:
                        key = "NONE";
                }
                comData = String.format("%s#", key);
                return false;
            }
        });
        this.scroll_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = TAG[6];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = TAG[12];
                        break;
                    default:
                        key = "NONE";
                }
                comData = String.format("%s#", key);
                return false;
            }
        });
        this.calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comData = String.format("%s#", TAG[7]);
                requestCalibrate = true;
            }
        });
        this.input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText taskEditText = new EditText(MouseActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MouseActivity.this)
                        .setTitle("INPUT")
                        .setView(taskEditText)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                comData = String.format("%s#%s", TAG[8], taskEditText.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
        this.backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comData = String.format("%s#", TAG[9]);
            }
        });
        this.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comData = String.format("%s#", TAG[10]);
            }
        });

        initSensorManager();

        this.UDPclient = SocketHandler.getUdpClient();
        this.TCPclient = SocketHandler.getTcpClient();
        this.UDPthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        if (!accData.equals("")) {
                            UDPclient.sendPacket(accData);
                            accData = "";
                        }
                        TimeUnit.MILLISECONDS.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.TCPthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (!comData.equals("")) {
                        TCPclient.sendCommand(comData);
                        comData = "";
                        try {
                            TimeUnit.MILLISECONDS.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        this.UDPthread.start();
        this.TCPthread.start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, linearAccelSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        AccX = sensorEvent.values[0];
        AccY = sensorEvent.values[1];

        if (cAccX == 0 && cAccY == 0 || requestCalibrate) {
            cAccX = AccX;
            cAccY = AccY;
            requestCalibrate = false;
            return ;
        }

        AccX -= cAccX;
        AccY -= cAccY;

        if (AccX < 0.8 && AccX > -0.8) {
            AccX = 0;
        }

        if (AccY < 0.8 && AccY > -0.8) {
            AccY = 0;
        }

        accData = String.format("%s#%.3f#%.3f", TAG[0], AccX, AccY);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void initSensorManager() {
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
    }
}
