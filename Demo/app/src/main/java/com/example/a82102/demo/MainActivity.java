package com.example.a82102.demo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    boolean change_check = false;
    SQLiteDatabase database;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceintent = new Intent(getApplicationContext(),Background.class);
        startService(serviceintent);

        final CalendarView calendarView = findViewById(R.id.calendarview);


        Button set_btn = findViewById(R.id.setting);
        final Button go_memo_btn = findViewById(R.id.go_memo_button);
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

        if(change_check == false) {
            go_memo_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleDateFormat df_year = new SimpleDateFormat("yyyy", Locale.KOREA);
                    SimpleDateFormat df_month = new SimpleDateFormat("M", Locale.KOREA);
                    SimpleDateFormat df_day = new SimpleDateFormat("d", Locale.KOREA);
                    int year = Integer.parseInt(df_year.format(new Date()));
                    int month = Integer.parseInt(df_month.format(new Date()));
                    int day = Integer.parseInt(df_day.format(new Date()));

                    Intent intent = new Intent(getApplicationContext(), Memo.class);
                    String intent_date = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
                    intent.putExtra("intent_date",intent_date);
                    intent.putExtra("year",year);
                    intent.putExtra("month",month);
                    intent.putExtra("day",day);
                    startActivity(intent);
                }
            });
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, final int year, final int month, final int day) {
                change_check = true;
                go_memo_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Memo.class);
                        String intent_date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                        intent.putExtra("intent_date",intent_date);
                        intent.putExtra("year",year);
                        intent.putExtra("month",month+1);
                        intent.putExtra("day",day);
                        startActivity(intent);
                    }
                });

                int db_date = Integer.parseInt(String.valueOf(year) + String.valueOf(month+1) + String.valueOf(day));

                textView = findViewById(R.id.textView);
                textView.setText("");
                openDatabase("databaseName");
                memoCreateTable();
                memoSelect(db_date);

            }
        });
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null) {
            Log.d("MyService", "데이터베이스 오픈됨.");
        }
    }

    private void memoCreateTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoSelect(int db_date) {
        Log.d("MyService", "memoSelect() 호출됨.");

        if (database != null) {
            String sql = "SELECT mac, title, contents, date FROM memo WHERE date = '" + db_date + "'";
            //String sql = "SELECT mac, title, contents FROM memo";
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String mac = cursor.getString(cursor.getColumnIndex("mac"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String contents = cursor.getString(cursor.getColumnIndex("contents"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                textView.append(mac + " / " + title + " / " + contents + " / " + date + "\n");
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }
}
