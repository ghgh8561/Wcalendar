package com.example.a82102.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceintent = new Intent(getApplicationContext(),Background.class);
        startService(serviceintent);

        CalendarView calendarView = findViewById(R.id.calendarview);


        Button set_btn = findViewById(R.id.setting);
        Button check = findViewById(R.id.checking);
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Set.class);
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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int day) {
                Intent intent = new Intent(getApplicationContext(), Memo.class);
                String intent_date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                intent.putExtra("intent_date",intent_date);
                intent.putExtra("year",year);
                intent.putExtra("month",month+1);
                intent.putExtra("day",day);
                startActivity(intent);
            }
        });
    }
}
