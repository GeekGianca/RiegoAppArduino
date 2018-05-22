package com.geekprogrammer.riegoapp.Services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.BluetoothActivity;
import com.geekprogrammer.riegoapp.Common.Common;
import com.geekprogrammer.riegoapp.Iterface.IHandlerBluetooth;
import com.geekprogrammer.riegoapp.Threads.BluetoothThreadConnection;

import java.io.IOException;
import java.util.UUID;

public class ResponseAndRequestHandlerBt implements IHandlerBluetooth{

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothThreadConnection connectedThread;
    private final UUID UUIDBT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int handlerState = 0;
    private StringBuilder dataStringInput = new StringBuilder();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("Read", "Handler");
            if (msg.what == handlerState) {
                String readMessage = (String) msg.obj;
                dataStringInput.append(readMessage);

                int endOfLineIndex = dataStringInput.indexOf("#");

                if (endOfLineIndex > 0) {
                    String inputData = dataStringInput.substring(0, endOfLineIndex);
                    Log.d("Data", inputData);
                    Toast.makeText(context, inputData, Toast.LENGTH_SHORT).show();
                    dataStringInput.delete(0, dataStringInput.length());
                }
            }
        }
    };

    public ResponseAndRequestHandlerBt(Context context) {
        this.context = context;
        bluetoothAdapter = getDefaultBluetoothAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(Common.mCurrentDevice.getDevice_uid());
        try {
            Log.d("MAC",Common.mCurrentDevice.getDevice_uid());
            bluetoothSocket = createConnectionSecure(device);
        } catch (IOException e) {
            Log.e("IOEx_Constructor", e.toString());
        }
        createConnectionBt();
    }

    @Override
    public BluetoothAdapter getDefaultBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public BluetoothSocket createConnectionSecure(BluetoothDevice bDevice) throws IOException {
        return bDevice.createRfcommSocketToServiceRecord(UUIDBT);
    }

    @Override
    public void createConnectionBt() {
        try {
            bluetoothSocket.connect();
            Log.e("Connection", "...................");
            connectedThread = new BluetoothThreadConnection(bluetoothSocket, handler, context, handlerState);
            connectedThread.start();
        } catch (IOException e) {
            Log.d("IOExc_CreateConnection",e.toString()+" "+e.toString());
            Toast.makeText(context, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int execute(String write) {
        connectedThread.write(write);
        return Integer.parseInt(write);
    }

    @Override
    public int close() {
        try {
            bluetoothSocket.close();
            Log.d("onPause","Connection Close");
        } catch (IOException e) {
            Log.d("IOException On Pause",e.toString());
            Toast.makeText(context, "Error: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
}
