package Guitar.Keyboard.Controller;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class Tuner {
    private final double DIVI = 8.192;
    private final int SAMPLE_SIZE = 8192;
    private final int SPECTRE_SIZE = SAMPLE_SIZE * 4;

    private final double freqMin = 77.781;
    private final double freqMax = 349.228;

    private KeyboardController controller;
    private final double MIN_AMPL = 1.0;

    public Tuner(KeyboardController controller) {
        this.controller = controller;
    }

    public void start() {
        AudioFormat audioFormat = new AudioFormat(8000.0F, 8, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);

            byte[] data = new byte[SPECTRE_SIZE];
            targetDataLine.start();
            double[] ar = new double[SPECTRE_SIZE];
            double[] ai = new double[SPECTRE_SIZE];
            Arrays.fill(ai, 0.0);
            while ((targetDataLine.read(data, 0, SAMPLE_SIZE) > 0)) {
                try {
                    for (int i = 0; i < SAMPLE_SIZE; i++) {
                        ar[i] = data[i];
                    }

                    computeFFT(SPECTRE_SIZE, ar, ai);
                    double maxAmpl = 0;
                    double maxIndex = 0;

                    for (int i = (int) (freqMin * DIVI); i < (freqMax * DIVI); i++) {
                        if (Math.abs(ai[i]) > maxAmpl) {
                            maxAmpl = Math.abs(ai[i]);
                            maxIndex = i;
                        }
                    }

                    if (maxAmpl > MIN_AMPL) {
                        double curFreq = maxIndex / DIVI;
                        Notes notes = new Notes();
                        boolean found = false;

                        if (!found) {
                            for (double d : notes.E) {
                                if (Math.abs(curFreq - d) <= 4) {
                                    System.out.print("Moving forward (E, ");
                                    controller.pressW();
                                    found = true;
                                    break;
                                } 
                            }
                        }

                        if (!found) {
                            for (double d : notes.A) {
                                if (Math.abs(curFreq - d) <= 4) {
                                    System.out.print("Moving left (A, ");
                                    controller.pressA();
                                    found = true;
                                    break;
                                } 
                            }
                        }

                        if (!found) {
                            for (double d : notes.D) {
                                if (Math.abs(curFreq - d) <= 4) {
                                    System.out.print("Moving back (D, ");
                                    controller.pressS();
                                    found = true;
                                    break;
                                } 
                            }
                        }

                        if (!found) {
                            for (double d : notes.G) {
                                if (Math.abs(curFreq - d) <= 4) {
                                    System.out.print("Moving right (G, ");
                                    controller.pressD();
                                    found = true;
                                    break;
                                } 
                            }
                        }

                        System.out.print(" hz: " + curFreq + ")\n");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                targetDataLine.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Notes {
        public final double[] A = new double[] { 27.5, 55, 110, 220, 440, 880, 1760 };
        public final double[] B = new double[] { 30.86771, 61.73541, 123.4708, 246.9417, 493.8833, 987.7666 };
        public final double[] C = new double[] { 32.70320, 65.40639, 130.8128, 261.6256, 523.2511, 1046.502 };
        public final double[] D = new double[] { 36.70810, 73.41619, 146.8324, 293.6648, 587.3295, 1174.659 };
        public final double[] E = new double[] { 41.20344, 82.40689, 164.8138, 329.6276, 659.2551, 1318.510 };
        public final double[] F = new double[] { 43.65353, 87.30706, 174.6141, 349.2282, 698.4565, 1396.913 };
        public final double[] G = new double[] { 48.99943, 97.99886, 195.9977, 391.9954, 783.9909, 1567.982 };
    }

    // https://github.com/mirkoebert/simpleguitartuner/blob/master/src/main/java/com/ebertp/simpleguitartuner/CaptureThread.java
    public static void computeFFT(final int n, final double[] ar, final double[] ai) {
        final double scale = 2.0 / n;
        int i, j;
        for (i = j = 0; i < n; ++i) {
            if (j >= i) {
                double tempr = ar[j] * scale;
                double tempi = ai[j] * scale;
                ar[j] = ar[i] * scale;
                ai[j] = ai[i] * scale;
                ar[i] = tempr;
                ai[i] = tempi;
            }
            int m = n / 2;
            while ((m >= 1) && (j >= m)) {
                j -= m;
                m /= 2;
            }
            j += m;
        }
        int mmax, istep;
        for (mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax) {
            double delta = Math.PI / mmax;
            for (int m = 0; m < mmax; ++m) {
                double w = m * delta;
                double wr = Math.cos(w);
                double wi = Math.sin(w);
                for (i = m; i < n; i += istep) {
                    j = i + mmax;
                    double tr = wr * ar[j] - wi * ai[j];
                    double ti = wr * ai[j] + wi * ar[j];
                    ar[j] = ar[i] - tr;
                    ai[j] = ai[i] - ti;
                    ar[i] += tr;
                    ai[i] += ti;
                }
            }
            mmax = istep;
        }
    }
}
