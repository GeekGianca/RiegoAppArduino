package com.geekprogrammer.riegoapp.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

import com.geekprogrammer.riegoapp.BluetoothActivity;
import com.geekprogrammer.riegoapp.DatetimeActivity;

public class AutomaticIrrigationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            Log.d("Start", "Receiver");
            MediaPlayer mediaPlayer = MediaPlayer.create(context,
                    Settings.System.DEFAULT_RINGTONE_URI);
            mediaPlayer.start();
            Intent newTask = new Intent(context, DatetimeActivity.class);
            newTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newTask);
        }catch(Exception e){
            Log.e("Exception", e.getMessage());
        }
    }
}
