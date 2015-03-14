package org.m3studio.signalanalyzer.DSP;

/**
 * Created by madmax on 10.03.15.
 */
public class MathHelper {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
