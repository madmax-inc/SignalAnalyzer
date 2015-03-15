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

    public static double adaptiveRound(double value) {
        int integerPart = (int) Math.round(value);

        if (integerPart != 0)
            return Math.round(value);

        double fraction = value - integerPart;
        int places = 0;

        while (((int) fraction) == 0) {
            fraction *= 10;
            places++;
        }

        return round(value, places);
    }
}
