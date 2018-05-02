package com.geekprogrammer.riegoapp.Threads;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothThreadConnection extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler handlerBluetoothIn;
    private Context context;
    private int handlerState;

    public BluetoothThreadConnection(BluetoothSocket socket, Handler handler, Context context, int state)
    {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        handlerBluetoothIn = handler;
        this.context = context;
        handlerState = state;
        try
        {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d("Exception Constructor",e.toString());
        }
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
                Log.d("Exception in thread",e.toString());
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
            Log.e("Write Error","Sin Conexion");
            Toast.makeText(context, "La Conexión fallo", Toast.LENGTH_LONG).show();
        }
    }
}
