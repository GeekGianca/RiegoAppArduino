package com.geekprogrammer.riegoapp.Model;

public class Devices {
    private String device_name;
    private String device_uid;

    public Devices(String device_name, String device_uid) {
        this.device_name = device_name;
        this.device_uid = device_uid;
    }

    public Devices() {
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_uid() {
        return device_uid;
    }

    public void setDevice_uid(String device_uid) {
        this.device_uid = device_uid;
    }
}
