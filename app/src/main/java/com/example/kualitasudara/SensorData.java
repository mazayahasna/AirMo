package com.example.kualitasudara;

public class SensorData {
    private int co2;
    private double pm2_5;
    private int voc;
    private String timestamp;

    public SensorData() {
    }

    public SensorData(int co2, double pm2_5, int voc, String timestamp) {
        this.co2 = co2;
        this.pm2_5 = pm2_5;
        this.voc = voc;
        this.timestamp = timestamp;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public int getVoc() {
        return voc;
    }

    public void setVoc(int voc) {
        this.voc = voc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
