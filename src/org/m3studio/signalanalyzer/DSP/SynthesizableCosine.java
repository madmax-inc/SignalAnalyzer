package org.m3studio.signalanalyzer.DSP;

/**
 * Created by madmax on 09.03.15.
 */
public class SynthesizableCosine extends SynthesizableSignal {

    public SynthesizableCosine() {
        super();
    }

    @Override
    public Signal synthesizeIn(Signal source) {
        double frequency = (Double) getProperty("frequency");
        double amplitude = (Double) getProperty("amplitude");
        double phase = (Double) getProperty("phase");

        int length = source.getLength();

        final double arg = 2 * Math.PI * frequency / length;

        for (int i = 0; i < length; i++) {
            source.getValue(i).set(amplitude * Math.cos(arg * i + phase), 0.0);
        }

        return source;
    }
}
