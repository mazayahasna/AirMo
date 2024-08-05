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

public class Voc extends AppCompatActivity {

    private LineChart lineChartVoc;
    private DatabaseReference databaseReference;
    private static final String TAG = "VocActivity";
    private SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voc);

        lineChartVoc = findViewById(R.id.lineChartVoc);
        databaseReference = FirebaseDatabase.getInstance().getReference("sensor/kode-unik");

        setupChart();
        loadDataFromFirebase();
    }

    private void setupChart() {
        lineChartVoc.getDescription().setEnabled(false);
        lineChartVoc.setTouchEnabled(true);
        lineChartVoc.setDragEnabled(true);
        lineChartVoc.setScaleEnabled(true);
        lineChartVoc.setPinchZoom(true);

        XAxis xAxis = lineChartVoc.getXAxis();
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

        YAxis leftAxis = lineChartVoc.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + " ppb";
            }
        });

        YAxis rightAxis = lineChartVoc.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> vocEntries = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String timestampStr = dataSnapshot.child("timestamp").getValue(String.class);
                    Float voc = dataSnapshot.child("voc").getValue(Float.class);

                    Log.d(TAG, "Timestamp: " + timestampStr + ", VOC: " + voc); // Log untuk debugging

                    try {
                        Date date = firebaseDateFormat.parse(timestampStr);
                        if (date != null && voc != null) {
                            vocEntries.add(new Entry(date.getTime(), voc));
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing timestamp", e);
                    }
                }

                if (!vocEntries.isEmpty()) {
                    LineDataSet vocDataSet = new LineDataSet(vocEntries, "VOC");
                    vocDataSet.setColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                    vocDataSet.setValueTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary_dark));

                    LineData lineData = new LineData(vocDataSet);
                    lineChartVoc.setData(lineData);
                    lineChartVoc.invalidate(); // Refresh chart
                } else {
                    lineChartVoc.clear();
                    lineChartVoc.invalidate();
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
