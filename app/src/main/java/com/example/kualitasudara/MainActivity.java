package com.example.kualitasudara;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonGetStarted = findViewById(R.id.button);
        buttonGetStarted.setOnClickListener(v -> {
            // Pindah ke StartedActivity
            Intent intent = new Intent(MainActivity.this, started.class);
            startActivity(intent);
        });
    }
}
