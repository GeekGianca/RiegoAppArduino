package com.geekprogrammer.riegoapp.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.geekprogrammer.riegoapp.Helper.NotificationHelper;
import com.geekprogrammer.riegoapp.R;

import java.util.Calendar;

public class ServicesInBackground extends IntentService {
    private NotificationHelper helper;
    private Notification mBuilder;

    public ServicesInBackground() {
        super("RiegoApp");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("Start Command", "Initializing");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle args = intent.getExtras();
        int hourFinal = args.getInt("hour");
        int minutsFinal = args.getInt("minuts");
        if (intent == null) {
            while (true) {
                Calendar cDatetime = Calendar.getInstance();
                int cHour = cDatetime.get(Calendar.HOUR_OF_DAY);
                int cMinutes = cDatetime.get(Calendar.MINUTE);
                if (cHour == hourFinal && cMinutes == minutsFinal) {
                    break;
                } else {
                    Log.d("Time", "Waiting");
                    Log.d("Hour Current", "" + cHour);
                    Log.d("Minuts Current", "" + cMinutes);
                    Log.d("Hour", "" + hourFinal);
                    Log.d("Minuts", "" + minutsFinal);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e("Error Exception", e.toString());
                    }
                }
            }
            Log.d("State Task", "-------End Task Null-------");
            return;
        }


        while (true) {
            Calendar cDatetime = Calendar.getInstance();
            int cHour = cDatetime.get(Calendar.HOUR_OF_DAY);
            int cMinutes = cDatetime.get(Calendar.MINUTE);
            if (cHour == hourFinal && cMinutes == minutsFinal) {
                break;
            } else {
                Log.d("Time", "Waiting");
                Log.d("Hour Current", "" + cHour);
                Log.d("Minuts Current", "" + cMinutes);
                Log.d("Hour", "" + hourFinal);
                Log.d("Minuts", "" + minutsFinal);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("Error Exception", e.toString());
                }
            }
        }
        Log.d("State Task", "-------End Task-------");
        try {
            helper = new NotificationHelper(this);
            Notification.Builder builder = helper.getGeekChannelNotification("Iniciando Riego", "El riego automatico esta iniciado");
            helper.getManager().notify(1, builder.build());
        }catch (Exception e){
            mBuilder = new NotificationCompat.Builder(this, helper.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_invert_colors_black)
                    .setContentTitle("Iniciando Riego")
                    .setContentText("El riego automatico esta iniciado")
                    .build();
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mBuilder.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(1, mBuilder);
        }
    }
}
