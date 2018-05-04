package com.geekprogrammer.riegoapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    ListView listBluetooth;
    private BluetoothAdapter bluetoothAdapter;
    SwipeRefreshLayout swipe;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        swipe = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
        recyclerView = (RecyclerView)view.findViewById(R.id.listBluetooth);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
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
        Log.d("Create:","entro al fragment");
        swipe.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
