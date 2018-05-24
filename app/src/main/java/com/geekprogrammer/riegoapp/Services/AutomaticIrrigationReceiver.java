package com.geekprogrammer.riegoapp.Services;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.BluetoothActivity;
import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.DatetimeActivity;
import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Helper.NotificationHelper;
import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.Model.Devices;

import java.util.Random;
import java.util.Set;

public class AutomaticIrrigationReceiver extends BroadcastReceiver {

    //private BluetoothAdapter bluetoothAdapter = null;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper db = new DatabaseHelper(context);
        this.context = context;
        Datetime datetime;
        try{
            Log.d("Start", "Receiver");
            String timeStart = intent.getStringExtra("timeStart");
            String dateStart = intent.getStringExtra("dateStart");
            datetime = db.getDatetime(timeStart, dateStart);
            Log.e("Datetime", datetime.toString());
            MediaPlayer mediaPlayer = MediaPlayer.create(context,
                    Settings.System.DEFAULT_RINGTONE_URI);
            mediaPlayer.start();
            NotificationHelper helper = new NotificationHelper(context);
            NotificationCompat.Builder builder = helper.getGeekChannelNotification("PigShower App", "Se inicio el riego automatico");
            helper.getManager().notify(new Random().nextInt(), builder.build());
            Intent newTask = new Intent(context, BluetoothActivity.class);
            newTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try{
                if (Common.mCurrentDevice != null){
                    newTask.putExtra("devices_address", Common.mCurrentDevice.getDevice_uid());
                }
            }catch (NullPointerException npe){
                Log.e("NPE",npe.getMessage());
            }
            newTask.putExtra("statusBt", "1");
            newTask.putExtra("timeBt", String.valueOf(datetime.getDuration()));
            newTask.putExtra("idBt", String.valueOf(datetime.getId()));
            context.startActivity(newTask);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
        }
    }

    /*public String getMacAddressDevice() {
        String macAddressDevice = null;
        try{
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0){
                for (BluetoothDevice device : pairedDevices){
                    if (device.getName().equals("PigShowerBt")){
                        macAddressDevice = device.getAddress();
                    }
                    Log.v("Devices: ", device.getName());
                }
            }
        }catch (Exception e){
            Log.e("Exception Task", e.getMessage());
            Toast.makeText(this.context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        bluetoothAdapter.cancelDiscovery();
        return macAddressDevice;
    }*/

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e("End Task","________________________________________");
    }
}
