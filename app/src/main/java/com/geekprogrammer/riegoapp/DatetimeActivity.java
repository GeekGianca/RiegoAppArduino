package com.geekprogrammer.riegoapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.Services.AutomaticIrrigationReceiver;
import com.geekprogrammer.riegoapp.ViewHolder.DatetimeAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatetimeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager lManager;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Datetime> listDatetime = new ArrayList<>();
    DatetimeAdapter adapter;
    Datetime datetime;
    DatabaseHelper dbHelper;

    //Button btnAdd;
    private Calendar cCurrentTime;
    Toolbar toolbar;
    Date fActual = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String feActual = "";
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Lista de fechas");
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDatetimes();
            }
        });

        dbHelper = new DatabaseHelper(this);

        //btnAdd = (Button)findViewById(R.id.btnAdd);
        recyclerView = (RecyclerView)findViewById(R.id.listDatetime);
        recyclerView.setHasFixedSize(true);
        lManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lManager);
        cCurrentTime = Calendar.getInstance();
        //Add to firebase
        feActual = dateFormat.format(fActual);
        Log.e("Format Date", feActual);
        loadDatetimes();
    }

    private void loadDatetimes() {
        listDatetime.clear();
        listDatetime = dbHelper.getTimes();
        adapter = new DatetimeAdapter(listDatetime, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add_datetime){
            addDatetime();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(DatetimeActivity.this, MainActivity.class));
        finish();
        return false;
    }

    private void addDatetime() {
        datetime = new Datetime();
        loadAlertDialogTime();
    }

    private void loadAlertDialogTime() {
        int hourCurrent = cCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minuteCurrent = cCurrentTime.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(DatetimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cCurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cCurrentTime.set(Calendar.MINUTE, minute);
                cCurrentTime.set(Calendar.SECOND, 0);
                datetime.setTime(DateFormat.getTimeInstance(DateFormat.SHORT).format(cCurrentTime.getTime()));
                Log.d("TIME", DateFormat.getTimeInstance(DateFormat.SHORT).format(cCurrentTime.getTime()));
                Log.d("TIME-TIME", ""+hourOfDay+":"+minute);
                loadAlertDialogDate();
                Toast.makeText(DatetimeActivity.this, hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
            }
        },hourCurrent, minuteCurrent, true);
        timePickerDialog.setTitle("¿Hora de Regado?");
        timePickerDialog.show();
    }

    private void timeIrrigation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Asigna la duracion en minutos")
                .setMessage("Recuerda racionalizar el agua para evitar perdidas")
                .setIcon(R.drawable.ic_access_time);
        LayoutInflater inflater = getLayoutInflater();
        View addTime = inflater.inflate(R.layout.layout_input_time, null);
        final EditText timeDuration = (EditText)addTime.findViewById(R.id.time_duration);
        builder.setView(addTime);
        builder.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DatetimeActivity.this, timeDuration.getText().toString(), Toast.LENGTH_SHORT).show();
                datetime.setDuration(Integer.parseInt(timeDuration.getText().toString()));
                datetime.setState("En espera");
                loadListDatetime();
            }
        });
        builder.show();
    }

    private void loadAlertDialogDate() {
        DatePickerDialog.OnDateSetListener dsListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String sMonth = "";
                String sDay;
                int cMonth = month+1;
                if (cMonth < 10){
                    sMonth = String.valueOf("0"+cMonth);
                }else{
                    sMonth = String.valueOf(cMonth);
                }
                if (dayOfMonth < 10){
                    sDay = String.valueOf("0"+dayOfMonth);
                }else{
                    sDay = String.valueOf(dayOfMonth);
                }

                String sYear = String.valueOf(year);
                String date = String.valueOf(sMonth+"/"+sDay+"/"+sYear);
                datetime.setDate(date);
                Log.d("Format Date",date);

                if (date.equalsIgnoreCase(feActual)){
                    Toast.makeText(DatetimeActivity.this, date, Toast.LENGTH_SHORT).show();
                }
                timeIrrigation();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(DatetimeActivity.this, dsListener,
                cCurrentTime.get(Calendar.YEAR),
                cCurrentTime.get(Calendar.MONTH),
                cCurrentTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("¿Fecha de Regado?");
        datePickerDialog.show();
    }

    private void loadListDatetime() {
        //CONFIGURE DESIGN PATTERN
        if (datetime.getDate().equals(Common.currentDate())){
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AutomaticIrrigationReceiver.class);
            intent.putExtra("timeStart", datetime.getTime());
            intent.putExtra("dateStart", datetime.getDate());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
            /*if (cCurrentTime.before(Calendar.getInstance())){
                cCurrentTime.add(Calendar.DATE, 1);
            }*/
            manager.setExact(AlarmManager.RTC_WAKEUP, cCurrentTime.getTimeInMillis(), pendingIntent);
            dbHelper.addDatetime(datetime);
            Toast.makeText(this, "Riego automatico configurado", Toast.LENGTH_LONG).show();
        }else{
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Espere...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);
            pDialog.show();
            new CheckDays().execute();
        }
        loadDatetimes();
    }

    private class CheckDays extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            try {

                int counterDays = -1;

                while (true) {
                    Thread.sleep(50);
                    Calendar temp = Calendar.getInstance();
                    temp.add(Calendar.DAY_OF_YEAR, counterDays);
                    Log.e("Dates", datetime.getDate() + " CompareTo " + dateFormat.format(temp.getTime())+" Days "+counterDays);
                    if (datetime.getDate().equals(dateFormat.format(temp.getTime()))) {
                        return "Esta fecha ya paso, no se agrego a la lista";
                    }
                    if (counterDays == -360) {
                        break;
                    }
                    publishProgress(counterDays);
                    counterDays--;
                }

                counterDays = 1;

                while (true) {
                    Thread.sleep(50);
                    Calendar temp = Calendar.getInstance();
                    temp.add(Calendar.DAY_OF_YEAR, counterDays);
                    Log.e("Dates", datetime.getDate() + " CompareTo " + dateFormat.format(temp.getTime())+" Days "+counterDays);
                    if (datetime.getDate().equals(dateFormat.format(temp.getTime()))) {
                        datetime.setState("Programada");
                        dbHelper.addDatetime(datetime);
                        return "Se agrego una nueva fecha de riego en " + counterDays + " dias";
                    }
                    if (counterDays == 360) {
                        break;
                    }
                    counterDays++;
                }
            }catch (InterruptedException e){
                Log.e("IntEx", e.getMessage());
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int value = values[0];
            String mssg = value % 3 == 0 ? "Verificando calendario" : "Cargando";
            pDialog.setMessage(mssg);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(DatetimeActivity.this, s, Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
            loadDatetimes();
        }
    }
}
