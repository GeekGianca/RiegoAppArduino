package com.geekprogrammer.riegoapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.Services.AutomaticIrrigationReceiver;
import com.geekprogrammer.riegoapp.Services.IrrigationServiceForeground;
import com.geekprogrammer.riegoapp.Threads.SyncronizeIrrigation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ProgressDialog mDialog;
    DatabaseHelper db;
    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Pig Shower");

        db = new DatabaseHelper(this);

        Common.getDevicePaired(this);
        Common.mCurrentDevice = db.getDevice("PigShowerBt");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //checkStatusConnection();
        Log.e("Current Time", Common.currentTime());
        Log.e("Current Date", Common.currentDate());
        Log.e("Date before today", Common.beforeDate(-1));
        Datetime dt = db.getDatetime("7:07 AM", "05/25/2018");
        String dte = dt != null ? dt.toString() : "null" ;
        Log.e("Get Database", dte);
        updateDateAndTimes();
        service = new Intent(this, IrrigationServiceForeground.class);
    }

    private void updateDateAndTimes() {
        int contains = db.getTimes().size();
        new SyncronizeIrrigation(this).execute(contains);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            Datetime dt = db.getDatetimeOnUpdateExecute(Common.currentDate(), Common.currentTime());
            if (dt != null) {
                Date date = dateFormat.parse(dt.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
                calendar.set(Calendar.SECOND, date.getSeconds());
                Log.d("GET FORMAT DATE", DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
                Log.d("HORA",String.valueOf(date.getHours()));
                Log.d("MINUTOS", String.valueOf(date.getMinutes()));
                Log.d("SEGUNDOS", String.valueOf(date.getSeconds()));

                //
                AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, AutomaticIrrigationReceiver.class);
                intent.putExtra("timeStart", dt.getTime());
                intent.putExtra("dateStart", dt.getDate());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
                manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Se agrego una fecha pendiente al riego automatico", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Log.e("OnResume", e.getMessage());
        }
    }

    private void showAlertDialog(String title, String messg, int icon, Calendar calendar, final Datetime datetime) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(title)
                .setMessage(messg)
                .setIcon(icon)
                .setCancelable(false);
        if (calendar != null){
            mBuilder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deleteDatetime(datetime.getId());
                }
            });
        }else{
            mBuilder.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Servicio", "Iniciado");
        //startService(new Intent(MainActivity.this, IrrigationServiceForeground.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.on_bluetooth) {
            //checkState();
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_regados) {
            try{
                mDialog = new ProgressDialog(this);
                mDialog.setTitle("Espere...");
                mDialog.setMessage("Verificando estado del bluetooth");
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                Intent btA = new Intent(MainActivity.this, BluetoothActivity.class);
                btA.putExtra("devices_address", Common.mCurrentDevice.getDevice_uid());
                startActivity(btA);
                finish();
            }catch (NullPointerException npe){
                Log.e("NullPE No Mac", npe.getMessage());
                Toast.makeText(this, "No hay MAC vinculada al Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_fechas_riego) {
            startActivity(new Intent(MainActivity.this, DatetimeActivity.class));
            finish();
        } else if (id == R.id.nav_dispositivos) {
            startActivity(new Intent(MainActivity.this, PairedActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            //mDialog.dismiss();
        }catch (NullPointerException npe){
            Log.e("NullPointerEx", npe.getMessage());
        }
    }

}
