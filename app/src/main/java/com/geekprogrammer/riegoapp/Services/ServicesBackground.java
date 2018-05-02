package com.geekprogrammer.riegoapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Helper.NotificationHelper;

public class ServicesBackground extends Service {
    private Context context = this;
    private NotificationHelper helper;

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        helper = new NotificationHelper(this);
        Notification.Builder builder = helper.getGeekChannelNotification("New Notification", "Cargando datos de riego");
        helper.getManager().notify(1, builder.build());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(context,"Destruido", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
