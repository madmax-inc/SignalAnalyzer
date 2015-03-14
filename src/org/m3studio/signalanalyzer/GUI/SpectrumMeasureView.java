package org.m3studio.signalanalyzer.GUI;

import org.m3studio.signalanalyzer.DSP.MathHelper;
import org.m3studio.signalanalyzer.DSP.Spectrum;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 10.03.15.
 */
public class SpectrumMeasureView extends JPanel {
    private Spectrum spectrum;
    private final JSpinner harmonicSelectorSpinner;
    private final JLabel amplitudeLabel;
    private final JLabel phaseLabel;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());

    public SpectrumMeasureView() {
        super();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        harmonicSelectorSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
        harmonicSelectorSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateMeasure();
            }
        });
        amplitudeLabel = new JLabel("0");
        phaseLabel = new JLabel("0");

        add(new JLabel(resourceBundle.getString("frequency") + ":"));
        add(Box.createHorizontalStrut(GUIOptions.horizontalStrut));
        add(harmonicSelectorSpinner);
        add(Box.createHorizontalGlue());
        add(new JLabel(resourceBundle.getString("amplitude") + ":"));
        add(Box.createHorizontalStrut(GUIOptions.horizontalStrut));
        add(amplitudeLabel);
        add(Box.createHorizontalGlue());
        add(new JLabel(resourceBundle.getString("phase") + ":"));
        add(phaseLabel);
    }

    public final void updateMeasure() {
        amplitudeLabel.setText(String.valueOf(MathHelper.round(spectrum.getRealAmplitude((Integer) harmonicSelectorSpinner.getValue()), GUIOptions.decimalPlaces)));
        phaseLabel.setText(String.valueOf(MathHelper.round(spectrum.getPhase((Integer) harmonicSelectorSpinner.getValue()), GUIOptions.decimalPlaces)));
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
        harmonicSelectorSpinner.setModel(new SpinnerNumberModel(0, 0, spectrum.getLength(), 1));
        updateMeasure();
    }
}
