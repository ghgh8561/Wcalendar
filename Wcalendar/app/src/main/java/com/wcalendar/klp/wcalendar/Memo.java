package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Memo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        button();
    }

    private void button() {
        Button memo_insert_button = findViewById(R.id.memo_insert_button);
        Button memo_cancel_button = findViewById(R.id.memo_cancel_button);
        ImageButton memo_wifi_imageButton = findViewById(R.id.memo_wifi_imageButton);

        //저장버튼
        memo_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //취소버튼
        memo_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //공유기버튼
        memo_wifi_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_choice_wifi = new Intent(getApplicationContext(), ChoiceWifi.class);
                startActivity(go_choice_wifi);
            }
        });
    }
}
