package org.m3studio.signalanalyzer.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.m3studio.signalanalyzer.DSP.Spectrum;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 07.03.15.
 */
public class SpectrumView extends ChartPanel {
    private Spectrum spectrum;

    private JFreeChart chart;
    private XYSeriesCollection dataset;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());


    public SpectrumView(String title) {
        super(null);

        dataset = new XYSeriesCollection();

        chart = ChartFactory.createXYBarChart(
                title,
                resourceBundle.getString("frequency"),
                false,
                resourceBundle.getString("power"),
                dataset
        );

        setChart(chart);
    }

    public SpectrumView(String title, Spectrum spectrum) {
        this(title);

        this.spectrum = spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public void updateSpectrumView() {
        dataset.removeAllSeries();

        XYSeries amplitudePart = new XYSeries(resourceBundle.getString("powerSpectrum"));

        for (int i = 0; i < spectrum.getLength(); i++) {
            amplitudePart.add(i, spectrum.getAbs(i));
        }

        dataset.addSeries(amplitudePart);

        repaint();
    }
}
