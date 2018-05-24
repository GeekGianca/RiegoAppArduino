package com.geekprogrammer.riegoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Helper.DatabaseHelper;
import com.geekprogrammer.riegoapp.Helper.NotificationHelper;
import com.geekprogrammer.riegoapp.Threads.BluetoothThreadConnection;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import io.paperdb.Paper;

public class BluetoothActivity extends AppCompatActivity {

    ProgressDialog pDialog;
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
    //private static final UUID UUIDBT = UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb");
    private static final UUID UUIDBT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String macAddress = null;

    private static final int REQUEST_ENABLE_BT = 1;

    private int idBt = -1;

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
        Paper.init(this);

        idDevice = (TextView)findViewById(R.id.text_control);
        btnOn = (Button)findViewById(R.id.btnOn);
        btnOff = (Button)findViewById(R.id.btnOff);
        buffered = (TextView)findViewById(R.id.buffered);
        //Read the last state
        String bred = Paper.book().read("textState");
        buffered.setText(bred);

        String address = getIntent().getStringExtra("devices_address");
        macAddress = address;
        idDevice.setText(address);
        Log.d("Device MAC",address);

        handlerBluetoothIn = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.d("Read", "Handler");
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
                Paper.book().destroy();
                Paper.book().write("STATE", 1);
                Paper.book().write("textState", "Riego Encendido");
                connectedThread.write("1");
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Paper.book().write("STATE", 0);
                Paper.book().write("textState", "Riego Apagado");
                connectedThread.write("0");
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BluetoothActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

    private BluetoothSocket createConnectionSecure(BluetoothDevice bDevice) throws IOException {
        Log.e("UUIDBT", ""+UUIDBT);
        return bDevice.createRfcommSocketToServiceRecord(UUIDBT);
    }

    private void returnToBlock() {
        Intent returnMain = new Intent(BluetoothActivity.this, PairedActivity.class);
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
            }else{
                Log.d("ONLINE", "=======================");
            }
        }
    }

    //Overrides
    @Override
    public void onResume() {
        super.onResume();
        Log.e("MAC", macAddress);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        try {
            bluetoothSocket = createConnectionSecure(device);
            bluetoothAdapter.cancelDiscovery();
            bluetoothSocket.connect();
            connectedThread = new BluetoothThreadConnection(bluetoothSocket, handlerBluetoothIn, BluetoothActivity.this, handlerState);
            connectedThread.start();
            TextView textConn = (TextView)findViewById(R.id.text_connection);
            textConn.setText("Bluetooth Conectado");
            startServiceAutomatic();
        } catch (IOException e) {
            Log.e("IOException onResume",e.toString());
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

    private void startServiceAutomatic() {
        //If connection start with automatic services
        if (getIntent().getStringExtra("statusBt") != null &&
                getIntent().getStringExtra("timeBt") != null &&
                getIntent().getStringExtra("idBt") != null){
            idBt = Integer.parseInt(getIntent().getStringExtra("idBt"));
            String statusBt = getIntent().getStringExtra("statusBt");
            int durationTask = Integer.parseInt(getIntent().getStringExtra("timeBt"))*60;
            Log.e("OnReceived", "Get Data "+statusBt+" - "+durationTask+" - "+idBt);
            activeIrrigationTime(statusBt, durationTask);

        }else{
            Log.d("No Service","Task not exist");
        }
    }

    private void activeIrrigationTime(String statusBt, int durationTask) {
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Ejecutando riego automatico");
        pDialog.setIcon(R.drawable.ic_pig);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();
        connectedThread.write(statusBt);
        new ExecuteIrrigationDuration().execute(durationTask);
    }

    private class ExecuteIrrigationDuration extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            long seconds = 0;
            int duration = integers[0];
            for (int i=1; i<=duration; i++){
                try {
                    int progress = (100*i)/duration;
                    long millis = 1000;
                    Thread.sleep(millis);
                    seconds += millis;
                    Log.d("Seconds", String.valueOf(seconds));
                    Log.d("Progress", String.valueOf(progress));
                    String tProgrss = progress == 100.0 ? "Ejecutado" : "Ejecutando al";
                    publishProgress(String.valueOf(progress), String.valueOf(duration), tProgrss);
                } catch (InterruptedException e) {
                    Log.e("Interrupted Ex", e.getMessage());
                }
            }
            return "0";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            float progress = Float.parseFloat(values[0]);
            float total = Float.valueOf(values[1]);

            String message = values[2];

            pDialog.setProgress((int)(progress));
            pDialog.setMessage(String.valueOf(message+" "+progress+"%"));
            if (String.valueOf(progress).equals("100.0")){
                pDialog.cancel();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            NotificationHelper helper = new NotificationHelper(BluetoothActivity.this);
            NotificationCompat.Builder builder = helper.getGeekChannelNotification("PigShower App", "El riego automatico ha finalizado");
            helper.getManager().notify(new Random().nextInt(), builder.build());
            connectedThread.write(s);
            //Update database
            DatabaseHelper dbHelper = new DatabaseHelper(BluetoothActivity.this);
            dbHelper.updateDatetime(idBt, "Ejecutada");
        }
    }
}
