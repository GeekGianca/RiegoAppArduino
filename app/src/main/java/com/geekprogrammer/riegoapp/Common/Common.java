package com.geekprogrammer.riegoapp.Common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Model.Devices;

import java.util.Set;

public class Common {
    public static Devices mCurrentDevice;

    public static void getDevicePaired(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        DatabaseHelper db = new DatabaseHelper(context);
        try{
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0){
                for (BluetoothDevice device : pairedDevices){
                    Devices dev = new Devices(device.getAddress(), device.getName(), 0);
                    db.addDevices(dev);
                    Log.v("Devices: ", device.getName());
                }
            }
        }catch (Exception e){
            Log.e("Exception Task", e.getMessage());
        }
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter = null;
    }
}
