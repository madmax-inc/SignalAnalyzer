package org.m3studio.signalanalyzer.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.m3studio.signalanalyzer.DSP.Complex;
import org.m3studio.signalanalyzer.DSP.Signal;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 06.03.15.
 */
public class SignalView extends ChartPanel {
    private Signal signal;

    private JFreeChart chart;
    private XYSeriesCollection dataset;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());


    public SignalView(String title) {
        super(null);

        dataset = new XYSeriesCollection();

        chart = ChartFactory.createXYLineChart(
                title,
                "t",
                resourceBundle.getString("signal"),
                dataset
        );

        setChart(chart);
    }

    public SignalView(String title, Signal signal) {
        this(title);

        this.signal = signal;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

    public void updateSignalView() {
        dataset.removeAllSeries();

        XYSeries realPart = new XYSeries("Re");
        XYSeries imaginaryPart = new XYSeries("Im");

        for (int i = 0; i < signal.getLength(); i++) {
            Complex c = signal.get(i);

            realPart.add(i, c.re());
            imaginaryPart.add(i, c.im());
        }

        dataset.addSeries(realPart);
        dataset.addSeries(imaginaryPart);

        repaint();
    }
}
