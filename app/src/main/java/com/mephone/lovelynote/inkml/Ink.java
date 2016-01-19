package com.mephone.lovelynote.inkml;

import java.util.LinkedList;

/**
 * @author huanghua
 */
public class Ink {

    private LinkedList<Trace> traces;

    public Ink() {
        traces = new LinkedList<>();
    }

    public LinkedList<Trace> getTraces() {
        return traces;
    }

    public void addTraces(Trace trace) {
        this.traces.add(trace);
    }
}
