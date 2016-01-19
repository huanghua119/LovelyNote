package com.mephone.lovelynote.inkml;

import java.util.LinkedList;

/**
 * @author huanghua
 */
public class Trace {

    private LinkedList<Point> points;

    public Trace() {
        points = new LinkedList<>();
    }


    public LinkedList<Point> getPoints() {
        return points;
    }

    public void addPoints(Point point) {
        points.add(point);
    }
}
