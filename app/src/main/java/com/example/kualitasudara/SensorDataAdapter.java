package com.example.kualitasudara;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SensorDataAdapter extends RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder> {

    private List<SensorData> sensorDataList;
    private String type;

    public SensorDataAdapter(List<SensorData> sensorDataList, String type) {
        this.sensorDataList = sensorDataList;
        this.type = type;
    }

    @NonNull
    @Override
    public SensorDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_data, parent, false);
        return new SensorDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorDataViewHolder holder, int position) {
        SensorData sensorData = sensorDataList.get(position);
        holder.textTimestamp.setText("Timestamp: " + sensorData.getTimestamp());

        if (type.equals("co2")) {
            holder.textValue.setText("CO2: " + sensorData.getCo2() + " ppm");
        } else if (type.equals("pm2_5")) {
            holder.textValue.setText("PM2.5: " + sensorData.getPm2_5() + " µg/m³");
        } else if (type.equals("voc")) {
            holder.textValue.setText("VOC: " + sensorData.getVoc() + " ppb");
        }
    }

    @Override
    public int getItemCount() {
        return sensorDataList.size();
    }

    public static class SensorDataViewHolder extends RecyclerView.ViewHolder {
        TextView textTimestamp, textValue;

        public SensorDataViewHolder(@NonNull View itemView) {
            super(itemView);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textValue = itemView.findViewById(R.id.text_value);
        }
    }
}
