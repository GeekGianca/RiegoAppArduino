package com.geekprogrammer.riegoapp.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geekprogrammer.riegoapp.Helper.NotificationHelper;

public class IrrigationServiceForeground extends Service {

    public IrrigationServiceForeground() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE","CREATED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("START","SERVICE IN BACKGROUND");
        new SyncronizeService().execute();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SyncronizeService extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            try{
                for (int i=0; i<200; i++){
                    Log.d("SERVICE","IS RUNNING");
                    Thread.sleep(1000);
                }
            }catch (InterruptedException e){
                Log.e("INTEXC", e.getMessage());
            }
            return "Done!";
        }
    }
}
