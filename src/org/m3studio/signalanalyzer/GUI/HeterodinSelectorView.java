package org.m3studio.signalanalyzer.GUI;

import org.m3studio.signalanalyzer.DSP.Signal;
import org.m3studio.signalanalyzer.DSP.SynthesizableComplexExponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 10.03.15.
 */
public class HeterodinSelectorView extends JPanel {
    private final SynthesizableComplexExponent heterodinParameter;
    private Signal heterodin;

    private final JSpinner heterodinFrequencySpinner;

    private HeterodinCallback callback;

    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());


    public HeterodinSelectorView() {
        super();

        heterodinParameter = new SynthesizableComplexExponent();
        heterodinParameter.setProperty("frequency", 0.0);
        heterodin = new Signal(1024);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        heterodinFrequencySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
        heterodinFrequencySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                heterodinParameter.setProperty("frequency", (Double) heterodinFrequencySpinner.getValue());

                if (callback != null)
                    callback.onHeterodinChanged();
            }
        });

        add(new JLabel(resourceBundle.getString("heterodinFrequency") + ":"));
        add(Box.createHorizontalGlue());
        add(heterodinFrequencySpinner);
    }

    public void changeHeterodinLength(int newLength) {
        heterodin = new Signal(newLength);
        heterodinParameter.synthesizeIn(heterodin);
    }

    public void setCallback(HeterodinCallback callback) {
        this.callback = callback;
    }

    public Signal getHeterodin() {
        heterodinParameter.synthesizeIn(heterodin);

        return heterodin;
    }

    public interface HeterodinCallback {
        public void onHeterodinChanged();
    }
}
