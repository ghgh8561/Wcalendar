package com.example.ho.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class testing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        Button wifi_btn = findViewById(R.id.wifi);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.wifiname);

        textView.setText(intent.getStringExtra("mac"));
        wifi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), wifi_listview.class);
                startActivity(intent);
            }
        });
    }
}
