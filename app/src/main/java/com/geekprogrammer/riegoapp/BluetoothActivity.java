package com.geekprogrammer.riegoapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Threads.BluetoothThreadConnection;

import java.io.IOException;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    //private ProgressDialog pDialog;
    TextView idDevice;
    Button btnOn;
    Button btnOff;
    Toolbar toolbar;
    TextView buffered;
    //Vars Need for bluetooth connection
    Handler handlerBluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private StringBuilder dataStringInput = new StringBuilder();
    //private ConnectedThread connectedThread;
    private BluetoothThreadConnection connectedThread;

    // Identificador unico de servicio - SPP UUID
    private static final UUID UUIDBT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String macAddress = null;

    private static final int REQUEST_ENABLE_BT = 1;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Riego manual");
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        idDevice = (TextView)findViewById(R.id.text_control);
        btnOn = (Button)findViewById(R.id.btnOn);
        btnOff = (Button)findViewById(R.id.btnOff);
        buffered = (TextView)findViewById(R.id.buffered);

        String address = getIntent().getStringExtra("devices_address");
        macAddress = address;
        idDevice.setText(address);
        Log.d("Device MAC",address);

        handlerBluetoothIn = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    dataStringInput.append(readMessage);

                    int endOfLineIndex = dataStringInput.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String inputData = dataStringInput.substring(0, endOfLineIndex);
                        Log.d("Data", inputData);
                        buffered.setText(inputData);
                        dataStringInput.delete(0, dataStringInput.length());
                    }
                }
            }
        };

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("1");
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedThread.write("0");
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkState();
    }

    private BluetoothSocket createConnectionSecure(BluetoothDevice bDevice) throws IOException {
        return bDevice.createRfcommSocketToServiceRecord(UUIDBT);
    }

    private void returnToBlock() {
        Intent returnMain = new Intent(BluetoothActivity.this, MainActivity.class);
        startActivity(returnMain);
        Toast.makeText(BluetoothActivity.this, "No disponible o Incompatible", Toast.LENGTH_LONG).show();
    }

    private void checkState() {
        if (bluetoothAdapter == null){
            Toast.makeText(BluetoothActivity.this, "El dispositivo no soporta el Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enablebt, REQUEST_ENABLE_BT);
            }/*else{
                Toast.makeText(getContext(), "En espera de Conexion", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    //Overrides
    @Override
    public void onResume() {
        super.onResume();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        try {
            bluetoothSocket = createConnectionSecure(device);
            bluetoothSocket.connect();
            connectedThread = new BluetoothThreadConnection(bluetoothSocket, handlerBluetoothIn, BluetoothActivity.this, handlerState);
            connectedThread.start();
        } catch (IOException e) {
            Log.d("IOException onResume",e.toString());
            Toast.makeText(BluetoothActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
            returnToBlock();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            bluetoothSocket.close();
            Log.d("onPause","Connection Close");
        } catch (IOException e) {
            Log.d("IOException On Pause",e.toString());
            Toast.makeText(BluetoothActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
            Log.d("BTSocket","Connection Close");
        } catch (IOException e) {
            Log.d("IOException on Destroy",e.toString());
            Toast.makeText(BluetoothActivity.this, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
