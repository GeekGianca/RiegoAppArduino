package com.geekprogrammer.riegoapp;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.ViewHolder.DatetimeAdapter;

import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatetimeFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager lManager;
    List<Datetime> listDatetime;
    DatetimeAdapter adapter;

    Button btnAdd;
    private Calendar cCurrentTime;

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
        loadListDatetime();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatetime();
            }
        });
    }

    private void addDatetime() {
        loadAlertDialogTime();
        loadAlertDialogDate();
    }

    private void loadAlertDialogDate() {
        DatePickerDialog.OnDateSetListener dsListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String sYear = String.valueOf(year);
                String sMonth = String.valueOf(month);
                String sDay = String.valueOf(dayOfMonth);
                Toast.makeText(getContext(), sYear+"-"+sMonth+"-"+sDay, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), hourOfDay+":"+minute, Toast.LENGTH_SHORT).show();
            }
        },hour, minute, true);
        timePickerDialog.setTitle("¿Hora de Regado?");
        timePickerDialog.show();
    }

    private void loadListDatetime() {
    }
}
