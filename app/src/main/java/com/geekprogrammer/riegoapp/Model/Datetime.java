package com.geekprogrammer.riegoapp.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Datetime {
    private int id;
    private String date;
    private String time;
    private int duration;
    private String state;
    public Map<String, Boolean> datetime = new HashMap<>();

    public Datetime() {
    }

    public Datetime(String date, String time, int duration, String state) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.state = state;
    }

    public Datetime(int id, String date, String time, int duration, String state) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Datetime{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", state='" + state + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("", date);
        result.put("", time);
        result.put("", duration);
        result.put("", state);
        return result;
    }
}
