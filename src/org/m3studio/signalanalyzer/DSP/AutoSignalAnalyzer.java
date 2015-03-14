package org.m3studio.signalanalyzer.DSP;

import java.util.ArrayList;

/**
 * Created by madmax on 07.03.15.
 */
public class AutoSignalAnalyzer {
    private Signal source;
    private double min;
    private double heterodinAccuracy;
    private int windowSize;

    public AutoSignalAnalyzer(Signal source, double min, double heterodinAccuracy, int windowSize) {
        this.source = source;
        this.min = min;
        this.heterodinAccuracy = heterodinAccuracy;
        this.windowSize = windowSize;
    }

    public ArrayList<SynthesizableSignal> detectHarmonics() {
        SignalCutter cutter = new SignalCutter(source, new Signal(source));

        SynthesizableComplexExponent heterodinParameter = new SynthesizableComplexExponent();
        heterodinParameter.setProperty("frequency", 0.0);

        Signal heterodin = new Signal(source.getLength());
        Signal heterodinedSignal = new Signal(cutter.getCurrentSignal());
        Spectrum spectrum = new Spectrum(heterodinedSignal);

        int harmonic;
        while ((harmonic = spectrum.detectStrongPeak(min)) != -1) {
            double heterodinSelected = 0.0;
            double signalToNoise = spectrum.getRealAmplitude(harmonic) / spectrum.getAverageAmplitudeIn(harmonic, windowSize);

            for (double heterodinFrequency = -0.5; heterodinFrequency < (0.5 + heterodinAccuracy); heterodinFrequency += heterodinAccuracy) {
                heterodinParameter.setProperty("frequency", heterodinFrequency);
                heterodinParameter.synthesizeIn(heterodin);
                heterodinedSignal.set(cutter.getCurrentSignal()).multiply(heterodin);
                spectrum.recalc();

                double newSignalToNoise = spectrum.getRealAmplitude(harmonic) / spectrum.getAverageAmplitudeIn(harmonic, windowSize);

                if (newSignalToNoise > signalToNoise) {
                    signalToNoise = newSignalToNoise;
                    heterodinSelected = heterodinFrequency;
                }
            }

            SynthesizableCosine parameter = new SynthesizableCosine();

            heterodinParameter.setProperty("frequency", heterodinSelected);
            heterodinParameter.synthesizeIn(heterodin);
            heterodinedSignal.set(cutter.getCurrentSignal()).multiply(heterodin);
            spectrum.recalc();

            parameter.setProperty("amplitude", MathHelper.adaptiveRound(spectrum.getRealAmplitude(harmonic)));
            parameter.setProperty("frequency", harmonic - heterodinSelected);
            parameter.setProperty("phase", MathHelper.round(spectrum.getPhase(harmonic), 1));

            cutter.addSignal(parameter);

            cutter.cutNext();
            heterodinedSignal.set(cutter.getCurrentSignal());
            spectrum.recalc();
        }

        return cutter.getSignalsParameters();
    }


}
