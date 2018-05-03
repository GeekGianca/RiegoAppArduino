package com.geekprogrammer.riegoapp.Model;

public class Datetime {
    private String date;
    private String time;
    private String duration;
    private String state;

    public Datetime() {
    }

    public Datetime(String date, String time, String duration, String state) {
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
