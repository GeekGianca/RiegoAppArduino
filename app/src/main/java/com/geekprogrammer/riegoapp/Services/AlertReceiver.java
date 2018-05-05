package com.geekprogrammer.riegoapp.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.geekprogrammer.riegoapp.DatetimeFragment;
import com.geekprogrammer.riegoapp.R;

public class AlertReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "Riego Automatico","Se iniciara el riego automatico,mantenga la App abierta o en segundo plano","¡Atencion!");
    }

    private void createNotification(Context context, String s, String s1, String alert) {
        PendingIntent notification = PendingIntent.getActivity(context, 0, new Intent(context, DatetimeFragment.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.ic_invert_colors_black)
        .setContentTitle(s)
        .setTicker(s1)
        .setContentText(alert);
        mBuilder.setContentIntent(notification);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, mBuilder.build());
    }
}
