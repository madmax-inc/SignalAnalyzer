package org.m3studio.signalanalyzer.DSP;

import java.util.ArrayList;

/**
 * Created by madmax on 10.03.15.
 */
public class SignalCutter {
    private Signal sourceSignal;
    private Signal outputSignal;

    private ArrayList<SynthesizableSignal> signalsParameters;
    private ArrayList<Signal> synthesizedSignals;

    private int cutCounter;

    public SignalCutter(Signal sourceSignal, Signal outputSignal) {
        this.sourceSignal = sourceSignal;
        this.outputSignal = outputSignal;

        signalsParameters = new ArrayList<SynthesizableSignal>();
        synthesizedSignals = new ArrayList<Signal>();

        cutCounter = 0;
    }

    public void reset() {
        outputSignal.set(sourceSignal);
        cutCounter = 0;
    }

    public Signal getSourceSignal() {
        return sourceSignal;
    }

    public Signal getCurrentSignal() {
        return outputSignal;
    }

    public ArrayList<SynthesizableSignal> getSignalsParameters() {
        return signalsParameters;
    }

    public void recutAll() {
        outputSignal.set(sourceSignal);

        for (Signal s : synthesizedSignals) {
            outputSignal.minus(s);
        }

        cutCounter = synthesizedSignals.size();
    }

    public boolean cutNext() {
        if (cutCounter >= synthesizedSignals.size())
            return false;

        outputSignal.minus(synthesizedSignals.get(cutCounter++));

        return true;
    }

    public void addSignal(SynthesizableSignal signal) {
        signalsParameters.add(signal);
        Signal newSignal = new Signal(sourceSignal.getLength());
        signal.synthesizeIn(newSignal);
        synthesizedSignals.add(newSignal);
    }

    public void removeSignal(SynthesizableSignal signal) {
        int index = signalsParameters.indexOf(signal);

        signalsParameters.remove(signal);
        synthesizedSignals.remove(index);
    }

    public void removeAll() {
        signalsParameters.clear();
        synthesizedSignals.clear();
    }

    public void regenerateSignal(SynthesizableSignal signal) {
        int index = signalsParameters.indexOf(signal);

        signal.synthesizeIn(synthesizedSignals.get(index));
    }
}
