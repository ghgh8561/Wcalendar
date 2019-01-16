package com.example.ho.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button set_btn = findViewById(R.id.setting);
        Button set_2_btn = findViewById(R.id.setting_2);
        final CalendarView calendarview = findViewById(R.id.calendarview);
        final TextView plan_list = findViewById(R.id.plan_list);




        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), setting.class);
                startActivity(intent);
            }
        });
        set_2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), testing.class);
                startActivity(intent);
            }
        });


        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int day) {
                month = 1 + month;
                plan_list.setText("Date: " + year + "/" + month + "/" + day);

                Toast.makeText(getApplicationContext(), month + "월" + day + "일", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
