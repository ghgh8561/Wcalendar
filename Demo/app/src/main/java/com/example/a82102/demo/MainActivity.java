package com.example.a82102.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceintent = new Intent(getApplicationContext(),Background.class);
        startService(serviceintent);

        Button set_btn = findViewById(R.id.setting);
        Button set_2_btn = findViewById(R.id.setting_2);
        Button check = findViewById(R.id.checking);
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Set.class);
                startActivity(intent);
            }
        });
        set_2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Checking_Wifi.class);
                startActivity(intent);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), test.class);
                startActivity(intent);
            }
        });
    }
}
