package com.geekprogrammer.riegoapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekprogrammer.riegoapp.Model.Devices;
import com.geekprogrammer.riegoapp.ViewHolder.DevicesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    List<Devices> devices = new ArrayList<>();
    DevicesAdapter adapter;

    private static final String TAG = "DevicesBluetooth";
    ListView listBluetooth;
    public static String EXTRA_DEVICES_ADDRESS = "devices_address";
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAd;
    SwipeRefreshLayout swipe;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        checkState();
    }
    private static final int REQUEST_ENABLE_BT = 1;

    private void checkState() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getContext(), "El dispositivo no soporta el Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (!bluetoothAdapter.isEnabled()){
                Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enablebt, REQUEST_ENABLE_BT);
            }else{
                Toast.makeText(getContext(), "Bluetooth Encendido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT){
            Toast.makeText(getContext(), "Bluetooth Encendido", Toast.LENGTH_SHORT).show();
        }
    }

    private AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DashboardFragment df = new DashboardFragment();
            Bundle deviceArgs = new Bundle();
            deviceArgs.putString(EXTRA_DEVICES_ADDRESS, address);
            df.setArguments(deviceArgs);
            ft.replace(R.id.screen_area, df);
            ft.commit();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        recyclerView = (RecyclerView)view.findViewById(R.id.listBluetooth);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        loadListDevices();
    }

    private void loadListDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                Devices dev = new Devices(device.getName(), device.getAddress());
                devices.add(dev);
                Log.d("Devices: ", device.getName());
            }
            adapter = new DevicesAdapter(devices, getContext(), getFragmentManager());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);

        }else{
            Log.d("No Devices: ", "Sin dispositivos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
