package org.m3studio.signalanalyzer.GUI;

import org.m3studio.signalanalyzer.DSP.SynthesizableCosine;
import org.m3studio.signalanalyzer.DSP.SynthesizableSignal;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by madmax on 10.03.15.
 */
public class CosineGeneratorView extends SignalGeneratorView {
    private JSpinner amplitudeSpinner;
    private JSpinner frequencySpinner;
    private JSpinner phaseSpinner;

    public CosineGeneratorView() {
        super();

        amplitudeSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
        amplitudeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSignalParameter("amplitude", amplitudeSpinner.getValue());
            }
        });

        frequencySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1024.0, 0.1));
        frequencySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSignalParameter("frequency", frequencySpinner.getValue());
            }
        });

        phaseSpinner = new JSpinner(new SpinnerNumberModel(0.0, -Math.PI, Math.PI, 0.1));
        phaseSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSignalParameter("phase", phaseSpinner.getValue());
            }
        });

        addComponent(new JLabel(resourceBundle.getString("amplitude") + ":"));
        addComponent(Box.createHorizontalStrut(GUIOptions.horizontalStrut));
        addComponent(amplitudeSpinner);

        addComponent(Box.createHorizontalGlue());

        addComponent(new JLabel(resourceBundle.getString("frequency") + ":"));
        addComponent(Box.createHorizontalStrut(GUIOptions.horizontalStrut));
        addComponent(frequencySpinner);

        addComponent(Box.createHorizontalGlue());

        addComponent(new JLabel(resourceBundle.getString("phase") + ":"));
        addComponent(Box.createHorizontalStrut(GUIOptions.horizontalStrut));
        addComponent(phaseSpinner);

        setSignalParameter("amplitude", 0.0);
        setSignalParameter("frequency", 0.0);
        setSignalParameter("phase", 0.0);
    }

    @Override
    protected SynthesizableSignal createParameter() {
        return new SynthesizableCosine();
    }

    @Override
    protected void updateGUI() {
        amplitudeSpinner.setValue(getSignalParameter("amplitude"));
        frequencySpinner.setValue(getSignalParameter("frequency"));
        phaseSpinner.setValue(getSignalParameter("phase"));
    }
}
