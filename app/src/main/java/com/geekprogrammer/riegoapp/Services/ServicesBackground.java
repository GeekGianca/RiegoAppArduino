package com.geekprogrammer.riegoapp.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.geekprogrammer.riegoapp.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServicesBackground extends Service {
    private Context context = this;
    private NotificationHelper helper;
    private Notification mBuilder;
    private Calendar cTime = Calendar.getInstance();

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(context, "Servicio iniciado", Toast.LENGTH_SHORT).show();
        Log.d("Servicio", "Iniciado");
        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this,
                1,
                alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT));
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
/*int cHour = cTime.get(Calendar.HOUR_OF_DAY);
        int minute = cTime.get(Calendar.MINUTE);
        String time = String.format("%s:%s",cHour,minute);
        Log.e("Time Background", time);
        int hr = 8;
        int min = 59;
        Toast.makeText(context, "Servicio en background", Toast.LENGTH_SHORT).show();
        while (true){
            if (cHour == hr && minute == min){
                try {
                    helper = new NotificationHelper(this);
                    Notification.Builder builder = helper.getGeekChannelNotification("New Notification", "Cargando datos de riego");
                    helper.getManager().notify(1, builder.build());
                }catch (Exception e){
                    mBuilder = new NotificationCompat.Builder(this, helper.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_invert_colors_black)
                            .setContentTitle("New Notification")
                            .setContentText("Cargando datos de riego")
                            .build();
                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    mBuilder.flags = Notification.FLAG_AUTO_CANCEL;
                    manager.notify(1, mBuilder);
                }
                break;
            }else{
                Log.e("Execute", "Task");
            }
        }*/