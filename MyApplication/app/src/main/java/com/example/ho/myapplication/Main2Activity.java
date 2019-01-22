package com.example.ho.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mContext = this;

        Button set_btn = findViewById(R.id.setting);
        final Button set_2_btn = findViewById(R.id.setting_2);
        final CalendarView calendarview = findViewById(R.id.calendarview);
        final TextView plan_list = findViewById(R.id.plan_list);



        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), setting.class);
                startActivity(intent);
            }
        });

         calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            final public void onSelectedDayChange(CalendarView CalendarView, final int year, int month, final int day) {
                month = 1 + month;
                plan_list.setText("Date: " + year + "/" + month + "/" + day);



                //캘린더뷰에서 일자 선택 시 일정으로
                if(plan_list.getText().toString().contains("Date")) {
                    final int finalMonth = month;
                    set_2_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), testing.class);
                            intent.putExtra("year",year);
                            intent.putExtra("month", finalMonth);
                            intent.putExtra("day",day);
                            startActivity(intent);
                        }
                    });

                }

                //일자 선택하지 않을 시 경고메세지
                if(!plan_list.getText().toString().contains("Date")) {
                    set_2_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(Main2Activity.this);
                            ad.setTitle("경고");
                            ad.setMessage("달력에서 일자를 선택해주세요");
                            AlertDialog dialog = ad.create();
                            dialog.show();

                            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                        }
                    });

                }

                Toast.makeText(getApplicationContext(), month + "월" + day + "일", Toast.LENGTH_SHORT).show();


            }
        });







    }
}
