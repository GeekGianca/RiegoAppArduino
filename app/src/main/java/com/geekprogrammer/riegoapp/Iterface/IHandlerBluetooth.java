package com.geekprogrammer.riegoapp.Iterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public interface IHandlerBluetooth {
    public BluetoothAdapter getDefaultBluetoothAdapter();
    public BluetoothSocket createConnectionSecure(BluetoothDevice bDevice) throws IOException;
    public void createConnectionBt();
    public int execute(String write);
    public int close();
}
