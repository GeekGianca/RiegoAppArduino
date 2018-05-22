package com.geekprogrammer.riegoapp.Model;

public class Devices {
    private String device_uid;
    private String device_name;
    private int status;

    public Devices(String device_uid, String device_name, int status) {
        this.device_uid = device_uid;
        this.device_name = device_name;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Devices{" +
                "device_name='" + device_name + '\'' +
                ", device_uid='" + device_uid + '\'' +
                ", status=" + status +
                '}';
    }
}
