package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Memo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        layout();

        button();
    }

    private void layout() {
        TextView memo_date = findViewById(R.id.memo_date);
        EditText memo_title = findViewById(R.id.memo_title);
        EditText memo_contents = findViewById(R.id.memo_contents);
        TextView memo_wifi_name = findViewById(R.id.memo_wifi_name);

        Intent Memo_intent = getIntent();
        int year = Memo_intent.getIntExtra("intent_year", 0);
        int month = Memo_intent.getIntExtra("intent_month", 0);
        int day = Memo_intent.getIntExtra("intent_day", 0);

        memo_date.append(String.valueOf(year) + String.valueOf(month) + String.valueOf(day));
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
