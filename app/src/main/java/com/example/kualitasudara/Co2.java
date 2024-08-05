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

public class Co2 extends AppCompatActivity {

    private LineChart lineChartCo2;
    private DatabaseReference databaseReference;
    private static final String TAG = "Co2Activity";
    private SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co2);

        lineChartCo2 = findViewById(R.id.lineChartCO2);
        databaseReference = FirebaseDatabase.getInstance().getReference("sensor/kode-unik");

        setupChart();
        loadDataFromFirebase();
    }

    private void setupChart() {
        lineChartCo2.getDescription().setEnabled(false);
        lineChartCo2.setTouchEnabled(true);
        lineChartCo2.setDragEnabled(true);
        lineChartCo2.setScaleEnabled(true);
        lineChartCo2.setPinchZoom(true);

        XAxis xAxis = lineChartCo2.getXAxis();
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

        YAxis leftAxis = lineChartCo2.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " ppm";
            }
        });

        YAxis rightAxis = lineChartCo2.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> co2Entries = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String timestampStr = dataSnapshot.child("timestamp").getValue(String.class);
                    Float co2 = dataSnapshot.child("co2").getValue(Float.class);

                    Log.d(TAG, "Timestamp: " + timestampStr + ", CO2: " + co2); // Log untuk debugging

                    try {
                        Date date = firebaseDateFormat.parse(timestampStr);
                        if (date != null && co2 != null) {
                            co2Entries.add(new Entry(date.getTime(), co2));
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing timestamp", e);
                    }
                }

                if (!co2Entries.isEmpty()) {
                    LineDataSet co2DataSet = new LineDataSet(co2Entries, "CO2");
                    co2DataSet.setColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                    co2DataSet.setValueTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary_dark));

                    LineData lineData = new LineData(co2DataSet);
                    lineChartCo2.setData(lineData);
                    lineChartCo2.invalidate(); // Refresh chart
                } else {
                    lineChartCo2.clear();
                    lineChartCo2.invalidate();
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
