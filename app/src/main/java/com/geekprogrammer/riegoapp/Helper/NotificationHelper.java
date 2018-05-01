package com.geekprogrammer.riegoapp.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.geekprogrammer.riegoapp.R;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.geekprogrammer.riegoapp.GEEK";
    private static final String CHANNEL_NAME = "GEEK Channel";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            nChannel.enableLights(true);
            nChannel.enableVibration(true);
            nChannel.setLightColor(Color.GREEN);
            nChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(nChannel);
        }

    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public Notification.Builder getGeekChannelNotification(String title, String body){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_invert_colors_black)
                    .setAutoCancel(true);
        }else{
            Log.d("Null Notification","Is null");
            return null;
        }
    }
}
