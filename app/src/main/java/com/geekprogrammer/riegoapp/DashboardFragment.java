package com.geekprogrammer.riegoapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Threads.BluetoothThreadConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    //private ProgressDialog pDialog;
    TextView idDevice;
    Button btnOn;
    Button btnOff;
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

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*pDialog = new ProgressDialog(getContext());
        pDialog.setTitle("Esperando conexion");
        pDialog.setMessage("Conectando...");
        pDialog.show();*/

        idDevice = (TextView)view.findViewById(R.id.text_control);
        idDevice.setText(getArguments().getString("devices_address"));
        btnOn = (Button)view.findViewById(R.id.btnOn);
        btnOff = (Button)view.findViewById(R.id.btnOff);
        buffered = (TextView)view.findViewById(R.id.buffered);
        macAddress = getArguments().getString("devices_address");
        Log.d("Device MAC",getArguments().getString("devices_address"));

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

    private void checkState() {
        if (bluetoothAdapter == null){
            Toast.makeText(getContext(), "El dispositivo no soporta el Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enablebt, REQUEST_ENABLE_BT);
            }/*else{
                Toast.makeText(getContext(), "En espera de Conexion", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        try {
            bluetoothSocket = createConnectionSecure(device);
            bluetoothSocket.connect();
        } catch (IOException e) {
            Log.d("IOException",e.toString());
            Toast.makeText(getContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
        connectedThread = new BluetoothThreadConnection(bluetoothSocket, handlerBluetoothIn, getContext(), handlerState);
        connectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            bluetoothSocket.close();
            Log.d("onPause","Connection Close");
        } catch (IOException e) {
            Log.d("IOException",e.toString());
            Toast.makeText(getContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            bluetoothSocket.close();
            Log.d("BTSocket","Connection Close");
        } catch (IOException e) {
            Log.d("IOException",e.toString());
            Toast.makeText(getContext(), "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //Create Security Connection
    private BluetoothSocket createConnectionSecure(BluetoothDevice bDevice) throws IOException {
        return bDevice.createRfcommSocketToServiceRecord(UUIDBT);
    }

    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    handlerBluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
            }
        }
    }
}


