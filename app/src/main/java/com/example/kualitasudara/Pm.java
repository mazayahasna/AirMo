package com.example.kualitasudara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pm extends AppCompatActivity {

    private LineChart lineChartPm;
    private DatabaseReference databaseReference;
    private static final String TAG = "PmActivity";
    private SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pm);

        lineChartPm = findViewById(R.id.lineChartPm);
        databaseReference = FirebaseDatabase.getInstance().getReference("sensor/kode-unik");

        setupChart();
        loadDataFromFirebase();
    }

    private void setupChart() {
        lineChartPm.getDescription().setEnabled(false);
        lineChartPm.setTouchEnabled(true);
        lineChartPm.setDragEnabled(true);
        lineChartPm.setScaleEnabled(true);
        lineChartPm.setPinchZoom(true);

        XAxis xAxis = lineChartPm.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                long timestamp = (long) value;
                return sdf.format(new Date(timestamp));
            }
        });

        YAxis leftAxis = lineChartPm.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " μg/m3";
            }
        });

        YAxis rightAxis = lineChartPm.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> pm2_5Entries = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String timestampStr = dataSnapshot.child("timestamp").getValue(String.class);
                    Float pm2_5 = dataSnapshot.child("pm2_5").getValue(Float.class);

                    Log.d(TAG, "Timestamp: " + timestampStr + ", pm2_5: " + pm2_5 ); // Log untuk debugging

                    try {
                        Date date = firebaseDateFormat.parse(timestampStr);
                        if (date != null && pm2_5  != null) {
                            pm2_5Entries.add(new Entry(date.getTime(), pm2_5 ));
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing timestamp", e);
                    }
                }

                if (!pm2_5Entries.isEmpty()) {
                    LineDataSet pm2_5DataSet = new LineDataSet(pm2_5Entries, "PM2_5");
                    pm2_5DataSet.setColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                    pm2_5DataSet.setValueTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary_dark));

                    LineData lineData = new LineData(pm2_5DataSet);
                    lineChartPm.setData(lineData);
                    lineChartPm.invalidate(); // Refresh chart
                } else {
                    lineChartPm.clear();
                    lineChartPm.invalidate();
                    Log.w(TAG, "No chart data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
