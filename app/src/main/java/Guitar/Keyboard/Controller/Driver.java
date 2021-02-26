package Guitar.Keyboard.Controller;

import java.awt.AWTException;

public class Driver {
    public static void main(String[] args) throws AWTException, InterruptedException {
        System.out.print("Starting in 3...");
        Thread.sleep(1000);
        System.out.print("2...");
        Thread.sleep(1000);
        System.out.print("1...");
        Thread.sleep(1000);
        Tuner tuner = new Tuner(new KeyboardController());
        tuner.start();
    }
}
