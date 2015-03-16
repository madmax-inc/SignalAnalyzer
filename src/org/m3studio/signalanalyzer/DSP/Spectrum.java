package org.m3studio.signalanalyzer.DSP;


/**
 * Created by madmax on 06.03.15.
 */
public class Spectrum {
    private Signal signal;
    private Complex spectrum[];

    public Spectrum(int length) {
        spectrum = new Complex[length];

        for (int i = 0; i < length; i++)
            spectrum[i] = new Complex();
    }

    public Spectrum(Signal signal) {
        recalcForNewSignal(signal);
    }

    public void recalc() {
        spectrum = fft(signal.toArray());
    }

    public void recalcForNewSignal(Signal signal) {
        this.signal = signal;
        spectrum = fft(signal.toArray());
    }

    public int getLength() {
        return spectrum.length;
    }

    public Complex get(int index) {
        return spectrum[index];
    }

    public double getRealAmplitude(int index) {
        double amplitude = spectrum[index].abs();

        if (index == 0)
            return amplitude / spectrum.length;
        else
            return (amplitude * 2) / spectrum.length;
    }

    public double getAbs(int index) {
        return spectrum[index].abs();
    }

    public double getPhase(int index) {
        return spectrum[index].arg();
    }

    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = Complex.plus(q[k], Complex.times(wk, r[k]));
            y[k + N/2] = Complex.minus(q[k], Complex.times(wk, r[k]));
        }
        return y;
    }

    public int detectStrongPeak(double min) {
        int peak = -1;

        for (int i = 0; i <= spectrum.length / 2; i++) {
            if (peak == -1) {
                if ((spectrum[i].abs() * 2 / spectrum.length)  > min)
                    peak = i;
            } else {
                if (spectrum[i].abs() > spectrum[peak].abs() && (spectrum[i].abs() * 2 / spectrum.length) > min)
                    peak = i;
            }
        }

        return peak;
    }

    public double getAverageAmplitudeIn(int harmonic, int windowSize) {
        int min = Math.max(harmonic - windowSize, 0);
        int max = Math.min(harmonic + windowSize, spectrum.length - 1);

        double avgAmplitude = 0.0;

        for (int i = min; i <= max; i++) {
            if (i == harmonic)
                continue;

            avgAmplitude += getRealAmplitude(i);
        }

        return (avgAmplitude / (max - min - 1));
    }

    private int[] getDistributionFunction(int confidence) {
        double amplitudeStep = getRealAmplitude(detectStrongPeak(0.0)) / confidence;
        int distributionFunction[] = new int[confidence];

        for (int i = 0; i < spectrum.length; i++) {
            int distributionPosition = Math.min(((int) Math.ceil(getRealAmplitude(i) / amplitudeStep)), confidence);

            for (int j = 1; j < distributionPosition; j++)
                distributionFunction[j]++;
        }

        return distributionFunction;
    }

    private int[] getDistributionDensity(int confidence) {
        int function[] = getDistributionFunction(confidence);
        int density[] = new int[confidence - 1];

        for (int i = 0; i < confidence - 1; i++) {
            density[i] = function[i + 1] - function[i];
        }

        return density;
    }

    public double estimatedNoise(int distributionConfidence, int peakTolerance, int maxDistance) {
        int density[] = getDistributionDensity(distributionConfidence);

        int peak = 0;

        for (int i = 0; i < density.length; i++)
            if (density[i] > density[peak])
                peak = i;

        int startingPeak = peak;
        int currentTolerance = 0;

        while (currentTolerance < peakTolerance && (peak - startingPeak) < maxDistance) {
            if (density[peak + 1] > density[peak])
                currentTolerance++;

            peak++;
        }

        return peak * getRealAmplitude(detectStrongPeak(0.0)) / distributionConfidence;
    }
}
