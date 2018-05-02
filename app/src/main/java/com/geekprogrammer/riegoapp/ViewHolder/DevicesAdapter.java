package com.geekprogrammer.riegoapp.ViewHolder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.geekprogrammer.riegoapp.DashboardFragment;
import com.geekprogrammer.riegoapp.Iterface.ItemClickListener;
import com.geekprogrammer.riegoapp.Model.Devices;
import com.geekprogrammer.riegoapp.R;

import java.util.ArrayList;
import java.util.List;

class DevicesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView deviceName;
    public TextView deviceUid;
    private ItemClickListener itemClickListener;

    public DevicesViewHolder(View itemView) {
        super(itemView);
        deviceName = (TextView)itemView.findViewById(R.id.deviceName);
        deviceUid = (TextView)itemView.findViewById(R.id.deviceUid);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}

public class DevicesAdapter extends RecyclerView.Adapter<DevicesViewHolder>{

    private List<Devices> listDevices = new ArrayList<>();
    private Context context;
    private FragmentManager fm;

    public DevicesAdapter(List<Devices> listDevices, Context context, FragmentManager fm) {
        this.listDevices = listDevices;
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item_bluetooth_layout, parent, false);
        return new DevicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesViewHolder holder, int position) {
        final String address = listDevices.get(position).getDevice_uid();
        holder.deviceUid.setText(listDevices.get(position).getDevice_uid());
        holder.deviceName.setText(listDevices.get(position).getDevice_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                DashboardFragment df = new DashboardFragment();
                Bundle deviceArgs = new Bundle();
                deviceArgs.putString("devices_address", address);
                df.setArguments(deviceArgs);
                ft.replace(R.id.screen_area, df);
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDevices.size();
    }
}
