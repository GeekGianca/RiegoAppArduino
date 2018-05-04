package com.geekprogrammer.riegoapp.Model;

public class Datetime {
    private String date;
    private String time;
    private int duration;
    private String state;

    public Datetime() {
    }

    public Datetime(String date, String time, int duration, String state) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
