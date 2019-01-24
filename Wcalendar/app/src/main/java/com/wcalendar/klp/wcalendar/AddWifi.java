package com.wcalendar.klp.wcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddWifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        button();
    }

    private void button() {
        Button addWifi_insert_button = findViewById(R.id.addWifi_insert_button);
        Button addWifi_cancel_button = findViewById(R.id.addWifi_cancel_button);

        //저장버튼
        addWifi_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //취소버튼
        addWifi_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
