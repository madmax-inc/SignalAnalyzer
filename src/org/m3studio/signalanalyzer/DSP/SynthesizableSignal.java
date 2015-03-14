package org.m3studio.signalanalyzer.DSP;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by madmax on 07.03.15.
 */
public abstract class SynthesizableSignal {
    private HashMap<String, Object> signalProperties;

    public SynthesizableSignal() {
        signalProperties = new HashMap<String, Object>();
    }

    public final Set<String> getProperties() {
        return signalProperties.keySet();
    }

    public final Object getProperty(String name) {
        return signalProperties.get(name);
    }

    public final void setProperty(String name, Object value) {
        signalProperties.put(name, value);
    }

    public final void set(SynthesizableSignal signal) {
        if (signal.getClass() != this.getClass())
            throw new RuntimeException("Exception occured while trying to set synthesizable signal!");

        for (Map.Entry<String, Object> property : signal.signalProperties.entrySet()) {
            signalProperties.put(property.getKey(), property.getValue());
        }
    }

    public abstract Signal synthesizeIn(Signal source);

    public final Signal synthesizeNew(int length) {
        return synthesizeIn(new Signal(length));
    }
}
