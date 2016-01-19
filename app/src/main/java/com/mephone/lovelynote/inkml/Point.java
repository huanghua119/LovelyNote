package com.mephone.lovelynote.inkml;

/**
 * @author huanghua
 */
public class Point {

    private float positionX;
    private float positionY;
    private long timestamp;
    private float pressure;

    public Point() {
    }

    public Point(String[] points) {
        if (points == null || points.length != 4) {
            return;
        }
        positionX = Float.parseFloat(points[0]);
        positionY = Float.parseFloat(points[1]);
        timestamp = Long.parseLong(points[2]);
        pressure = Float.parseFloat(points[3]);
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "positionX:" + positionX + " positionY:" + positionY;
    }
}
