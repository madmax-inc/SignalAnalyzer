package org.m3studio.signalanalyzer.GUI;

import org.m3studio.signalanalyzer.DSP.SynthesizableSignal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 07.03.15.
 */
public abstract class SignalGeneratorView extends JPanel {
    protected static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());
    private SynthesizableSignal signal;
    private int componentNo;

    private GeneratorCallback callback;

    public SignalGeneratorView() {
        super();
        componentNo = 0;
        signal = createParameter();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton applyButton = new JButton(resourceBundle.getString("applyButton"));
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null)
                    callback.onParametersChanged(SignalGeneratorView.this);
            }
        });
        add(applyButton);

        JButton removeButton = new JButton(resourceBundle.getString("removeButton"));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null)
                    callback.onRemoveGenerator(SignalGeneratorView.this);
            }
        });
        add(removeButton);
    }

    protected abstract SynthesizableSignal createParameter();
    protected abstract void updateGUI();

    public final void setSignalParameters(SynthesizableSignal signal) {
        this.signal = signal;
        updateGUI();
    }

    public final SynthesizableSignal getSignalParameters() {
        return signal;
    }

    public final void setCallback(GeneratorCallback callback) {
        this.callback = callback;
    }

    protected final void addComponent(Component component) {
        add(component, componentNo++);
    }

    protected final void setSignalParameter(String name, Object value) {
        signal.setProperty(name, value);
    }

    protected final Object getSignalParameter(String name) {
        return signal.getProperty(name);
    }

    public interface GeneratorCallback {
        public void onParametersChanged(SignalGeneratorView view);
        public void onRemoveGenerator(SignalGeneratorView view);
    }
}
