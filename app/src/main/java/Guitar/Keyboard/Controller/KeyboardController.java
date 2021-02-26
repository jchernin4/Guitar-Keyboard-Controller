package Guitar.Keyboard.Controller;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyboardController {
    private Robot robot;
    public KeyboardController() throws AWTException {
        this.robot = new Robot();
    }

    public void pressW() throws InterruptedException {
        robot.keyPress(KeyEvent.VK_W);
        Thread.sleep(1000);
        robot.keyRelease(KeyEvent.VK_W);
    }

    public void pressA() throws InterruptedException {
        robot.keyPress(KeyEvent.VK_A);
        Thread.sleep(1000);
        robot.keyRelease(KeyEvent.VK_A);
    }

    public void pressS() throws InterruptedException {
        robot.keyPress(KeyEvent.VK_S);
        Thread.sleep(1000);
        robot.keyRelease(KeyEvent.VK_S);
    }
    
    public void pressD() throws InterruptedException {
        robot.keyPress(KeyEvent.VK_D);
        Thread.sleep(1000);
        robot.keyRelease(KeyEvent.VK_D);
    }
}
