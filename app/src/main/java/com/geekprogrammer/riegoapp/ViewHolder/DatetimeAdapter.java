package com.geekprogrammer.riegoapp.ViewHolder;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekprogrammer.riegoapp.Model.Datetime;
import com.geekprogrammer.riegoapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

class DatetimeViewHolder extends RecyclerView.ViewHolder{

    public TextView date;
    public TextView time;
    public TextView duration;
    public TextView state;

    public DatetimeViewHolder(View itemView) {
        super(itemView);
        date = (TextView)itemView.findViewById(R.id.date_text);
        time = (TextView)itemView.findViewById(R.id.time_text);
        duration = (TextView)itemView.findViewById(R.id.duration_text);
        state = (TextView)itemView.findViewById(R.id.state_text);
    }

}
public class DatetimeAdapter extends RecyclerView.Adapter<DatetimeViewHolder> {

    List<Datetime> listDates = new ArrayList<>();
    Context context;

    public DatetimeAdapter(List<Datetime> listDates, Context context) {
        this.listDates = listDates;
        this.context = context;
    }

    @NonNull
    @Override
    public DatetimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_datetime_layout, parent, false);
        return new DatetimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DatetimeViewHolder holder, int position) {
        holder.date.setText(String.format("Fecha: %s",listDates.get(position).getDate()));
        holder.time.setText(String.format("Hora: %s",listDates.get(position).getTime()));
        holder.duration.setText(String.format("Duracion: %s m",listDates.get(position).getDuration()));
        holder.state.setText(String.format("Estado: %s",listDates.get(position).getState()));
    }

    @Override
    public int getItemCount() {
        return listDates.size();
    }
}
