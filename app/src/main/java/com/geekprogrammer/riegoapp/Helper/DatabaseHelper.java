package com.geekprogrammer.riegoapp.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.Model.Devices;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DB_NAME = "pigshowerdb.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Datetime> getTimes(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"id_shower", "date_shower", "time_shower", "duration_shower", "status_shower"};
        String table = "datetime_table";
        qb.setTables(table);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        List<Datetime> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new Datetime(c.getString(c.getColumnIndex("date_shower")),
                        c.getString(c.getColumnIndex("time_shower")),
                        c.getInt(c.getColumnIndex("duration_shower")),
                        c.getString(c.getColumnIndex("status_shower"))));
            }while (c.moveToNext());
        }
        db.close();
        return result;
    }

    public void addDatetime(Datetime dt){
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = String.format("INSERT INTO datetime_table(date_shower, time_shower, duration_shower, status_shower) VALUES('%s', '%s', %s, '%s');",
                    dt.getDate(),
                    dt.getTime(),
                    dt.getDuration(),
                    dt.getState());
            db.execSQL(query);
            db.close();
        }catch (Exception e){
            Log.e("Exception Db", e.getMessage());
        }
    }

    public Datetime getDatetime(String timeStart){
        SQLiteDatabase db = getReadableDatabase();
        String[] sqlSelect = {"id_shower", "date_shower", "time_shower", "duration_shower", "status_shower"};
        Cursor c = db.query("datetime_table", sqlSelect, "time_shower=?", new String[]{timeStart}, null, null, null, null);
        Datetime datetime = null;
        try{
            if (c != null){
                c.moveToFirst();
                datetime = new Datetime(c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3),
                        c.getString(4));
            }
            db.close();
            return datetime;
        }catch (CursorIndexOutOfBoundsException cioobe){
            Log.e("No table data",cioobe.getMessage());
            db.close();
            return datetime;
        }
    }

    public int updateDatetime(int id){
        int rs = -1;
        try{
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("status_shower", "Ejecutado");
            rs = db.update("datetime_table",values, "id_shower="+id, null);
            db.close();
        }catch (Exception e){
            Log.e("Update Db", e.getMessage());
        }
        return rs;
    }

    public void cleanDatetime(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM datetime_table");
        db.execSQL(query);
        db.close();
    }

    public List<Devices> getDevices(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"address_device", "name_device", "status_device"};
        String table = "devices_table";
        qb.setTables(table);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        List<Devices> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new Devices(c.getString(c.getColumnIndex("address_device")),
                        c.getString(c.getColumnIndex("name_device")),
                        c.getInt(c.getColumnIndex("status_device"))));
            }while (c.moveToNext());
        }
        db.close();
        return result;
    }

    public void addDevices(Devices d){
        try {
            Log.d("Devices Add", "UID: "+d.getDevice_uid()+" NAME: "+d.getDevice_name());
            SQLiteDatabase db = getReadableDatabase();
            String query = String.format("INSERT INTO devices_table(address_device, name_device, status_device) VALUES('%s', '%s', %s);",
                    d.getDevice_uid(),
                    d.getDevice_name(),
                    d.getStatus());
            db.execSQL(query);
            db.close();
        }catch (Exception e){
            Log.e("Exception Db", e.getMessage());
        }
    }

    public Devices getDevice(String name_device){
        SQLiteDatabase db = getReadableDatabase();
        String[] sqlSelect = {"address_device", "name_device", "status_device"};
        Cursor c = db.query("devices_table", sqlSelect, "name_device=?", new String[]{name_device}, null, null, null, null);
        Devices devices = null;
        try{
            if (c != null){
                c.moveToFirst();
                devices = new Devices(c.getString(0),
                        c.getString(1),
                        c.getInt(2));
            }
            db.close();
            return devices;
        }catch (CursorIndexOutOfBoundsException cioobe){
            Log.e("No table data",cioobe.getMessage());
            db.close();
            return devices;
        }
    }

    public void cleanDevices(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM devices_table");
        db.execSQL(query);
        db.close();
    }
}
