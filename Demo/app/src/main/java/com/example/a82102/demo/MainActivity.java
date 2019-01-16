package com.example.a82102.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static Activity _MainActivity;
    boolean change_check = false;
    boolean update_check = false;
    SQLiteDatabase database;
    String mac;
    String title;
    String contents;
    String date;
    String macName;
    String time;

    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> total = new ArrayList<>();
    ListView main_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _MainActivity = MainActivity.this;

        Intent serviceintent = new Intent(getApplicationContext(),Background.class);
        startService(serviceintent);

        final CalendarView calendarView = findViewById(R.id.calendarview);

        main_listView = findViewById(R.id.main_listView);
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
                    intent.putExtra("update_check",update_check);
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
                        intent.putExtra("update_check",update_check);
                        startActivity(intent);
                    }
                });

                int db_date = Integer.parseInt(String.valueOf(year) + String.valueOf(month+1) + String.valueOf(day));

                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, total);
                total.clear();
                items.clear();
                openDatabase("databaseName");
                memoCreateTable();
                memoSelect(db_date);
                main_listView.setAdapter(arrayAdapter);

                main_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        update_check = true;
                        macSelect();
                        Intent intent = new Intent(getApplicationContext(), Memo.class);
                        String intent_date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                        intent.putExtra("intent_date",intent_date);
                        intent.putExtra("year",year);
                        intent.putExtra("month",month+1);
                        intent.putExtra("day",day);
                        intent.putExtra("update_check",update_check);
                        intent.putExtra("macName", macName);
                        intent.putExtra("title",items.get((position*5) + 1));
                        intent.putExtra("contents",items.get((position*5) + 2));
                        intent.putExtra("time",items.get((position*5) + 4));

                        startActivity(intent);
                    }
                });

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
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer, time integer)";
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
            String sql = "SELECT mac, title, contents, date, time FROM memo WHERE date = '" + db_date + "'";
            //String sql = "SELECT mac, title, contents FROM memo";
            Cursor cursor = database.rawQuery(sql, null);

            int j = 0;

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToNext();
                mac = cursor.getString(cursor.getColumnIndex("mac"));
                title = cursor.getString(cursor.getColumnIndex("title"));
                contents = cursor.getString(cursor.getColumnIndex("contents"));
                date = cursor.getString(cursor.getColumnIndex("date"));
                time = cursor.getString(cursor.getColumnIndex("time"));
                items.add(mac);
                items.add(title);
                items.add(contents);
                items.add(date);
                items.add(time);
                total.add(items.get(j) + items.get(j+1) + items.get(j+2) + items.get(j+3));
                j = j+5;
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void macSelect() {
        if (database != null) {
            String sql = "Select mac, macName FROM mac WHERE mac = '" + items.get(0) + "'";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    macName = cursor.getString(cursor.getColumnIndex("macName"));
                }
            }
        }
    }
}
