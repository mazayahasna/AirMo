package com.example.kualitasudara;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class started extends AppCompatActivity {
    private TextView vocTextView, co2TextView, pmTextView, relayStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);

        vocTextView = findViewById(R.id.voc);
        co2TextView = findViewById(R.id.co2);
        pmTextView = findViewById(R.id.pm);
        relayStatusTextView = findViewById(R.id.relay_status);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference sensorRef = database.getReference("sensor/kode-unik");

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        DataSnapshot latestSnapshot = null;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            latestSnapshot = child;
                        }

                        if (latestSnapshot != null) {
                            Integer vocValue = latestSnapshot.child("voc").getValue(Integer.class);
                            if (vocValue != null) {
                                vocTextView.setText(String.valueOf(vocValue));
                            } else {
                                vocTextView.setText("Data tidak tersedia");
                            }

                            Integer co2Value = latestSnapshot.child("co2").getValue(Integer.class);
                            if (co2Value != null) {
                                co2TextView.setText(String.valueOf(co2Value));
                            } else {
                                co2TextView.setText("Data tidak tersedia");
                            }

                            Double pmValue = latestSnapshot.child("pm2_5").getValue(Double.class);
                            if (pmValue != null) {
                                pmTextView.setText(String.valueOf(pmValue));
                            } else {
                                pmTextView.setText("Data tidak tersedia");
                            }

                            String relayStatus = latestSnapshot.child("kondisi_relay").getValue(String.class);
                            if (relayStatus != null) {
                                relayStatusTextView.setText(relayStatus.equals("hidup") ? "Hidup" : "Mati");
                            } else {
                                relayStatusTextView.setText("Data tidak tersedia");
                            }
                        } else {
                            vocTextView.setText("Data tidak tersedia");
                            co2TextView.setText("Data tidak tersedia");
                            pmTextView.setText("Data tidak tersedia");
                            relayStatusTextView.setText("Data tidak tersedia");
                        }
                    } else {
                        vocTextView.setText("Data tidak tersedia");
                        co2TextView.setText("Data tidak tersedia");
                        pmTextView.setText("Data tidak tersedia");
                        relayStatusTextView.setText("Data tidak tersedia");
                    }
                } catch (Exception e) {
                    vocTextView.setText("Terjadi kesalahan");
                    co2TextView.setText("Terjadi kesalahan");
                    pmTextView.setText("Terjadi kesalahan");
                    relayStatusTextView.setText("Terjadi kesalahan");
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                vocTextView.setText("Gagal memuat data");
                co2TextView.setText("Gagal memuat data");
                pmTextView.setText("Gagal memuat data");
                relayStatusTextView.setText("Gagal memuat data");
                error.toException().printStackTrace();
            }
        });
    }

    public void move(View view) {
        Intent intent = new Intent(started.this, started.class);
        startActivity(intent);
    }

    public void Voc(View view) {
        Intent intent = new Intent(started.this, Voc.class);
        startActivity(intent);
    }

    public void Pm(View view) {
        Intent intent = new Intent(started.this, Pm.class);
        startActivity(intent);
    }

    public void Co2(View view) {
        Intent intent = new Intent(started.this, Co2.class);
        startActivity(intent);
    }
}

