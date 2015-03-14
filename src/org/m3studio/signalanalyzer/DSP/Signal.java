package org.m3studio.signalanalyzer.DSP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by madmax on 05.03.15.
 */
public class Signal {
    private Complex signalValues[];

    public Signal(int length) {
        signalValues = new Complex[length];

        for (int i = 0; i < length; i++)
            signalValues[i] = new Complex();
    }

    public Signal(Signal copyThis) {
        signalValues = new Complex[copyThis.signalValues.length];

        for (int i = 0; i < signalValues.length; i++)
            signalValues[i] = new Complex(copyThis.signalValues[i]);
    }

    public Signal(Complex signalValues[]) {
        this.signalValues = signalValues;
    }

    public Signal(double signalValues[]) {
        this.signalValues = new Complex[signalValues.length];

        for (int i = 0; i < signalValues.length; i++) {
            this.signalValues[i] = new Complex(signalValues[i], 0.0);
        }
    }

    public Signal(Collection<Double> signalValues) {
        this.signalValues = new Complex[signalValues.size()];
        int i = 0;

        for (Double signalVal : signalValues) {
            this.signalValues[i++] =  new Complex(signalVal, 0.0);
        }
    }

    public double getReal(int i) {
        return signalValues[i].re();
    }

    public double getImaginary(int i) {
        return signalValues[i].im();
    }

    public Complex get(int i) {
        return signalValues[i];
    }

    public int getLength() {
        return signalValues.length;
    }

    public double realMean() {
        double mean = 0.0;

        for (Complex x : signalValues) {
            mean += x.re();
        }

        mean /= signalValues.length;

        return mean;
    }

    public double standardDeviation() {
        double mean = realMean();

        double deviation = 0.0;

        for (Complex x : signalValues) {
            deviation += (x.re() - mean) * (x.re() - mean);
        }

        deviation /= signalValues.length;

        return Math.sqrt(deviation);
    }

    public Complex getValue(int index) {
        return signalValues[index];
    }

    public void setValue(int index, Complex value) {
        signalValues[index].set(value);
    }

    public Signal set(Signal b) {
        if (b.signalValues.length != signalValues.length)
            throw new RuntimeException("Signals are not the same length!");

        for (int i = 0; i < signalValues.length; i++)
            signalValues[i].set(b.signalValues[i]);

        return this;
    }

    public Signal minus(Signal b) {
        if (b.signalValues.length != signalValues.length)
            throw new RuntimeException("Signals are not the same length!");

        for (int i = 0; i < signalValues.length; i++)
            signalValues[i].minus(b.signalValues[i]);

        return this;
    }

    public Signal multiply(Signal b) {
        if (b.signalValues.length != signalValues.length)
            throw new RuntimeException("Signals are not the same length!");

        for (int i = 0; i < signalValues.length; i++)
            signalValues[i].times(b.signalValues[i]);

        return this;
    }


    public static Signal loadFile(String filename) throws FileNotFoundException {
        ArrayList<Double> signal = new ArrayList<Double>();

        BufferedReader reader = new BufferedReader(new FileReader(filename));

        try {
            while (reader.ready()) {
                String line = reader.readLine();
                line = line.trim();

                signal.add(Double.parseDouble(line));
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Signal(signal);
    }

    public Complex[] toArray () {
        return signalValues;
    }
}
