import javax.tools.Tool;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Executor {
    protected Robot robot;
    protected Dimension screen;
    protected Point location;
    protected double x, y;

    protected boolean scrolling = false;

    protected Thread thread;

    public Executor() throws AWTException {
        this.robot = new Robot();
        this.screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    location = MouseInfo.getPointerInfo().getLocation();
                    x = location.getX();
                    y = location.getY();
                }
            }
        });
        this.thread.start();
    }

    public void execute(String data) {
        String[] command = data.split("#");
        System.out.print(command[0] + '\r');
        switch (command[0]) {
            // mouse
            case "LEFT_DOWN":
                this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case "RIGHT_DOWN":
                this.robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                // this.robot.keyPress(KeyEvent.VK_Z);
                break;
            case "LEFT_UP":
                this.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case "RIGHT_UP":
                this.robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                // this.robot.keyRelease(KeyEvent.VK_Z);
                break;
            case "SCROLL_UP_START":
                this.scrolling = true;
                Thread scroll_up = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (scrolling) {
                            robot.keyPress(KeyEvent.VK_UP);
                            robot.delay(100);
                            robot.keyRelease(KeyEvent.VK_UP);
                        }
                    }
                });
                scroll_up.start();
                break;
            case "SCROLL_DOWN_START":
                this.scrolling = true;
                Thread scroll_down = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (scrolling) {
                            robot.keyPress(KeyEvent.VK_DOWN);
                            robot.delay(100);
                            robot.keyRelease(KeyEvent.VK_DOWN);
                        }
                    }
                });
                scroll_down.start();
                break;
            case "SCROLL_UP_STOP":
            case "SCROLL_DOWN_STOP":
                this.scrolling = false;
                break;
            case "CALIBRATE":
                this.robot.mouseMove((int) this.screen.getWidth() / 2, (int) this.screen.getHeight() / 2);
                break;
            case "INPUT":
                String text = command[1];
                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, stringSelection);
                // robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                // robot.keyRelease(KeyEvent.VK_META);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                break;
            case "BACKSPACE":
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                break;
            case "ENTER":
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
                break;
            case "MOVE":
                double _x = Double.parseDouble(command[1]);
                double _y = Double.parseDouble(command[2]);
                if (_y > 10) {
                    _y = 10;
                } else if (_y < -10) {
                    _y = -10;
                }
                if (_x < 0) {
                    _x = _x * 2;
                }
                this.x = Math.round(this.x + _y * 2);
                this.y = Math.round(this.y - _x * 2);
                this.robot.mouseMove((int) this.x, (int) this.y);
                System.out.print("d_x: " + _x + ", d_y: " + _y + '\r');
                break;
            // gamepad
            // up down left right
            case "GP1_U":
                robot.keyPress(KeyEvent.VK_UP);
                break;
            case "GP2_U":
                robot.keyRelease(KeyEvent.VK_UP);
                break;
            case "GP1_D":
                robot.keyPress(KeyEvent.VK_DOWN);
                break;
            case "GP2_D":
                robot.keyRelease(KeyEvent.VK_DOWN);
                break;
            case "GP1_L":
                robot.keyPress(KeyEvent.VK_LEFT);
                break;
            case "GP2_L":
                robot.keyRelease(KeyEvent.VK_LEFT);
                break;
            case "GP1_R":
                robot.keyPress(KeyEvent.VK_RIGHT);
                break;
            case "GP2_R":
                robot.keyRelease(KeyEvent.VK_RIGHT);
                break;
            // abxy
            case "GP1_A":
                robot.keyPress(KeyEvent.VK_C);
                break;
            case "GP2_A":
                robot.keyRelease(KeyEvent.VK_C);
                break;
            case "GP1_B":
                robot.keyPress(KeyEvent.VK_X);
                break;
            case "GP2_B":
                robot.keyRelease(KeyEvent.VK_X);
                break;
            case "GP1_X":
                robot.keyPress(KeyEvent.VK_Z);
                break;
            case "GP2_X":
                robot.keyRelease(KeyEvent.VK_Z);
                break;
            case "GP1_Y":
                robot.keyPress(KeyEvent.VK_X);
                break;
            case "GP2_Y":
                robot.keyRelease(KeyEvent.VK_X);
                break;
            // select start
            case "GP1_Start":
                robot.keyPress(KeyEvent.VK_ENTER);
                break;
            case "GP2_Start":
                robot.keyRelease(KeyEvent.VK_ENTER);
                break;
            case "GP1_Select":
                robot.keyPress(KeyEvent.VK_ESCAPE);
                break;
            case "GP2_Select":
                robot.keyRelease(KeyEvent.VK_ESCAPE);
                break;
            // select start
            case "GP1_LT":
                robot.keyPress(KeyEvent.VK_Q);
                break;
            case "GP2_LT":
                robot.keyRelease(KeyEvent.VK_Q);
                break;
            case "GP1_RT":
                robot.keyPress(KeyEvent.VK_E);
                break;
            case "GP2_RT":
                robot.keyRelease(KeyEvent.VK_E);
                break;
            // joystick
            case "JS_R":
                double angle_r = Double.parseDouble(command[1]);
                if (angle_r < 0) {
                    robot.keyRelease(KeyEvent.VK_UP);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                    robot.keyRelease(KeyEvent.VK_LEFT);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                } else if (angle_r < 22.5) {
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                } else if (angle_r < 22.5 + 45) {
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                } else if (angle_r < 22.5 + 45 * 2) {
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                } else if (angle_r < 22.5 + 45 * 3) {
                    robot.keyPress(KeyEvent.VK_LEFT);
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_LEFT);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                } else if (angle_r < 22.5 + 45 * 4) {
                    robot.keyPress(KeyEvent.VK_LEFT);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_LEFT);
                } else if (angle_r < 22.5 + 45 * 5) {
                    robot.keyPress(KeyEvent.VK_LEFT);
                    robot.keyPress(KeyEvent.VK_UP);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_LEFT);
                    robot.keyRelease(KeyEvent.VK_UP);
                } else if (angle_r < 22.5 + 45 * 6) {
                    robot.keyPress(KeyEvent.VK_UP);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_UP);
                } else if (angle_r < 22.5 + 45 * 7) {
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.keyPress(KeyEvent.VK_UP);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                    robot.keyRelease(KeyEvent.VK_UP);
                } else {
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    robot.delay(100);
                    robot.keyRelease(KeyEvent.VK_RIGHT);
                }
                break;
            case "JS_L":
                double angle_l = Double.parseDouble(command[1]);
                if (angle_l < 0) {
                    robot.keyRelease(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_D);
                } else if (angle_l < 22.5) {
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                } else if (angle_l < 22.5 + 45) {
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (angle_l < 22.5 + 45 * 2) {
                    robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (angle_l < 22.5 + 45 * 3) {
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_S);
                } else if (angle_l < 22.5 + 45 * 4) {
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                } else if (angle_l < 22.5 + 45 * 5) {
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_W);
                } else if (angle_l < 22.5 + 45 * 6) {
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                    robot.keyPress(KeyEvent.VK_A);
                    robot.keyRelease(KeyEvent.VK_A);
                } else if (angle_l < 22.5 + 45 * 7) {
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_W);
                } else {
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                    robot.keyPress(KeyEvent.VK_D);
                    robot.keyRelease(KeyEvent.VK_D);
                }
                break;
        }
    }
}
