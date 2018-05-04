package com.geekprogrammer.riegoapp;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.ViewHolder.DatetimeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatetimeFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager lManager;
    List<Datetime> listDatetime = new ArrayList<>();
    DatetimeAdapter adapter;
    Datetime datetime;

    Button btnAdd;
    private Calendar cCurrentTime;

    Date fActual = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String feActual = "";

    public DatetimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_datetime, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        recyclerView = (RecyclerView)view.findViewById(R.id.listDatetime);
        recyclerView.setHasFixedSize(true);
        lManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lManager);
        cCurrentTime = Calendar.getInstance();
        //Add to firebase
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatetime();
            }
        });
        feActual = dateFormat.format(fActual);
        Log.d("Format Date", feActual);
    }

    private void addDatetime() {
        datetime = new Datetime();
        loadAlertDialogTime();
    }

    private void timeIrration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Asigna la duracion en minutos")
                .setMessage("Recuerda racionalizar el agua para evitar perdidas")
                .setIcon(R.drawable.ic_access_time);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View addTime = inflater.inflate(R.layout.layout_input_time, null);
        final EditText timeDuration = (EditText)addTime.findViewById(R.id.time_duration);
        builder.setView(addTime);
        builder.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), timeDuration.getText().toString(), Toast.LENGTH_SHORT).show();
                datetime.setDuration(Integer.parseInt(timeDuration.getText().toString()));
                datetime.setState("En espera");
                listDatetime.add(datetime);
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
                String date = String.valueOf(sDay+"/"+sMonth+"/"+sYear);
                datetime.setDate(date);
                Log.d("Format Date",date);

                if (date.equalsIgnoreCase(feActual)){
                    Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
                }
                timeIrration();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dsListener,
                cCurrentTime.get(Calendar.YEAR),
                cCurrentTime.get(Calendar.MONTH),
                cCurrentTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.setTitle("¿Fecha de Regado?");
        datePickerDialog.show();
    }

    private void loadAlertDialogTime() {
        int hour = cCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = cCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = "";
                if (minute < 10){
                    String minuts = String.format("0%s",minute);
                     time = String.valueOf(hourOfDay+":"+minuts);
                }else{
                    time = String.valueOf(hourOfDay+":"+minute);
                }
                datetime.setTime(time);
                loadAlertDialogDate();
                Toast.makeText(getContext(), hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
            }
        },hour, minute, true);
        timePickerDialog.setTitle("¿Hora de Regado?");
        timePickerDialog.show();
    }

    private void loadListDatetime() {
        adapter = new DatetimeAdapter(listDatetime, getContext(), getFragmentManager());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
