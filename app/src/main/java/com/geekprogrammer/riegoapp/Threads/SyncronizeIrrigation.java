package com.geekprogrammer.riegoapp.Threads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Model.Datetime;

public class SyncronizeIrrigation extends AsyncTask<Integer, String, String> {

    private Context context;

    public SyncronizeIrrigation(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Integer... integers) {
        DatabaseHelper db = new DatabaseHelper(context);
        int size = integers[0];
        try{
            for (int i=0; i<size; i++){
                Datetime dt = db.getDatetimeOnUpdateExecute(Common.currentDate(), "Programada");
                if (dt != null){
                    db.updateDatetime(dt.getId(), "En espera");
                    Log.e("Update to", dt.getId()+" "+dt.getDate());
                }
                Log.e("Search","=========>");
                Thread.sleep(1000);
            }
            db.close();
            return "Update Complete!";
        }catch (InterruptedException e){
            Log.e("IntException", e.getMessage());
            return e.getMessage();
        }
    }
}
