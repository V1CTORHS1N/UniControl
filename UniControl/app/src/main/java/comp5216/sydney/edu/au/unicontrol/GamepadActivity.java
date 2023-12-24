package comp5216.sydney.edu.au.unicontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import com.kongqw.rockerlibrary.view.RockerView;

public class GamepadActivity extends Activity {
    private MaterialButton buttonL, buttonR, buttonU, buttonD, buttonX, buttonY, buttonA, buttonB;
    private MaterialButton buttonLT, buttonRT, buttonStart, buttonSelect;
    private RockerView rockerViewR, rockerViewL;

    private String[] GPTAGUntouched = {"GP1_A", "GP1_B", "GP1_X", "GP1_Y", "GP1_R", "GP1_L", "GP1_U", "GP1_D", "GP1_RT", "GP1_LT" ,"GP1_Start", "GP1_Select"};
    private String[] GPTAGTouched = {"GP2_A", "GP2_B", "GP2_X", "GP2_Y", "GP2_R", "GP2_L", "GP2_U", "GP2_D", "GP2_RT", "GP2_LT","GP2_Start", "GP2_Select"};
    private String[] JSTAG={"JS_L","JS_R"};
    private String ip, accData = "", comData = "";
    private int port;
    private UDPClient UDPclient;
    private TCPClient TCPclient;
    private Thread UDPthread, TCPthread;
    private boolean running = false, requestCalibrate = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("Creating gamepad");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamepad);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.buttonA = (MaterialButton) findViewById(R.id.buttonA);
        this.buttonB = (MaterialButton) findViewById(R.id.buttonB);
        this.buttonX = (MaterialButton) findViewById(R.id.buttonX);
        this.buttonY = (MaterialButton) findViewById(R.id.buttonY);
        this.buttonR = (MaterialButton) findViewById(R.id.buttonR);
        this.buttonL = (MaterialButton) findViewById(R.id.buttonL);
        this.buttonU = (MaterialButton) findViewById(R.id.buttonU);
        this.buttonD = (MaterialButton) findViewById(R.id.buttonD);
        this.buttonRT = (MaterialButton) findViewById(R.id.buttonRT);
        this.buttonLT = (MaterialButton) findViewById(R.id.buttonLT);
        this.buttonStart = (MaterialButton) findViewById(R.id.buttonStart);
        this.buttonSelect = (MaterialButton) findViewById(R.id.buttonSelect);

        rockerViewR = (RockerView) findViewById(R.id.rockerViewR);
        rockerViewL = (RockerView) findViewById(R.id.rockerViewL);
        rockerViewL.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_MOVE);
        rockerViewR.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_MOVE);
        accData = "";
        if (rockerViewR != null) {
            rockerViewR.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void angle(double angle) {
                    accData = String.format("%s#%.3f", JSTAG[1], angle);
                }

                @Override
                public void onFinish() {
                    accData = String.format("%s#%d", JSTAG[1], -1);
                }
            });
        }

        if (rockerViewL != null) {
            rockerViewL.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void angle(double angle) {
                    accData = String.format("%s#%.3f", JSTAG[0], angle);
                }

                @Override
                public void onFinish() {
                    accData = String.format("%s#%d", JSTAG[0], -1);
                }
            });
        }

        this.buttonA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[0];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[0];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);
                return false;
            }
        });
        this.buttonB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[1];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[1];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonX.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[2];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[2];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonY.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[3];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[3];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[4];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[4];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[5];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[5];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonU.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[6];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[6];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[7];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[7];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);
                return false;
            }
        });
        this.buttonRT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[8];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[8];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonLT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[9];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[9];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);

                return false;
            }
        });
        this.buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[10];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[10];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);
                return false;
            }
        });
        this.buttonSelect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String key = "";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        key = GPTAGUntouched[11];
                        break;
                    case MotionEvent.ACTION_UP:
                        key = GPTAGTouched[11];
                        break;
                    default:
                        key = "NONE";
                }
                accData = String.format("%s#", key);
                return false;
            }
        });

        this.UDPclient = SocketHandler.getUdpClient();
        this.TCPclient = SocketHandler.getTcpClient();

        this.UDPthread = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while (running) {
                    try {
                        if (!accData.equals("")) {
                            UDPclient.sendPacket(accData);
                            accData = "";
                        }
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.TCPthread = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while (running) {
                    if (!accData.equals("")) {
                        TCPclient.sendCommand(accData);
                        accData = "";
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
//        this.UDPthread.start();
        this.TCPthread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.running = false;
    }
}