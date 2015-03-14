package org.m3studio.signalanalyzer.DSP;

/**
 * Created by madmax on 10.03.15.
 */
public class SynthesizableComplexExponent extends SynthesizableSignal {

    public SynthesizableComplexExponent() {
        super();
    }

    @Override
    public Signal synthesizeIn(Signal source) {
        double frequency = (Double) getProperty("frequency");

        int length = source.getLength();

        final double arg = 2 * Math.PI * frequency / length;

        for (int i = 0; i < length; i++) {
            source.getValue(i).set(Math.cos(arg * i), Math.sin(arg * i));
        }

        return source;
    }
}
