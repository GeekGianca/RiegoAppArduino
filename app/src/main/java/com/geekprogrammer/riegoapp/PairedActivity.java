package com.geekprogrammer.riegoapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Model.Devices;
import com.geekprogrammer.riegoapp.ViewHolder.DevicesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PairedActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView stateBluetooth;
    Toolbar toolbar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    List<Devices> devices = new ArrayList<>();
    DevicesAdapter adapter;

    SwipeRefreshLayout swipe;

    private void loadListDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                Devices dev = new Devices(device.getName(), device.getAddress());
                devices.add(dev);
                Log.v("Devices: ", device.getName());
            }
            adapter = new DevicesAdapter(devices, this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }else{
            Log.d("No Devices: ", "Sin dispositivos");
        }
        swipe.setRefreshing(false);
    }

    private void checkState() {
        if (bluetoothAdapter == null){
            Toast.makeText(this, "El dispositivo no soporta el Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enablebt, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Dispositivos Emparejados");
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        stateBluetooth = (TextView)findViewById(R.id.bluetoothState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkState();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        recyclerView = (RecyclerView)findViewById(R.id.listBluetooth);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                devices.clear();
                loadListDevices();
            }
        });
        loadListDevices();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Servicio", "Iniciado");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT && bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth Encendido", Toast.LENGTH_SHORT).show();
            loadListDevices();
            stateBluetooth.setVisibility(View.INVISIBLE);
        }else{
            stateBluetooth.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No se Activo el Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PairedActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

}
