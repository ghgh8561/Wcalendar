package com.example.a82102.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Memo extends AppCompatActivity {
    public static Activity _memo;
    SQLiteDatabase database;
    EditText editText_title;
    EditText editText_contents;
    TextView date_textview;
    String mac;
    String time;
    int date;

    boolean update_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _memo = Memo.this;

        setContentView(R.layout.activity_memo);

        Button wifi_btn = findViewById(R.id.wifi);
        Intent intent = getIntent();
        final TextView textView = findViewById(R.id.wifi_name);
        textView.setText(intent.getStringExtra("macName"));
        mac = intent.getStringExtra("mac");
        update_check = intent.getBooleanExtra("update_check", false);
        time = intent.getStringExtra("time");

        date_textview = findViewById(R.id.date_textView);
        date_textview.setText(intent.getStringExtra("intent_date"));
        date = Integer.parseInt(String.valueOf(intent.getIntExtra("year", 0)) + String.valueOf(intent.getIntExtra("month", 0)) + String.valueOf(intent.getIntExtra("day", 0)));

        editText_title = findViewById(R.id.memo_title);
        editText_contents = findViewById(R.id.memo_contents);
        editText_title.setText(intent.getStringExtra("title"));
        editText_contents.setText(intent.getStringExtra("contents"));

        wifi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Checking_Wifi.class);
                String title = editText_title.getText().toString().trim();
                String contents = editText_contents.getText().toString().trim();
                String intent_date = date_textview.getText().toString().trim();
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
                intent.putExtra("intent_date",intent_date);
                intent.putExtra("date",date);
                intent.putExtra("update_check",update_check);
                startActivity(intent);
            }
        });

        openDatabase("databaseName");

        Button insert_db_button = findViewById(R.id.insert_db_button);
        insert_db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                if(update_check == false) { //memo 저장
                    String title = editText_title.getText().toString().trim();
                    String contents = editText_contents.getText().toString().trim();
                    SimpleDateFormat df_time = new SimpleDateFormat("HHmmss", Locale.KOREA);
                    int time = Integer.parseInt(df_time.format(new Date()));
                    createTable();
                    if(date != 0) {
                        memoInsert(mac, title, contents, date, time);
                    } else {
                        memoInsert(mac, title, contents, intent.getIntExtra("date", 0), time);
                    }

                    MainActivity mainActivity = (MainActivity) MainActivity._MainActivity;
                    mainActivity.finish();

                    finish();

                    startActivity(intent_main);
                }
                else { //memo 수정
                    String title = editText_title.getText().toString().trim();
                    String contents = editText_contents.getText().toString().trim();
                    String macName = textView.getText().toString().trim();
                    macSelect(macName);
                    memoUpdate(mac, title, contents, time);

                    MainActivity mainActivity = (MainActivity) MainActivity._MainActivity;
                    mainActivity.finish();

                    finish();

                    startActivity(intent_main);
                }
            }
        });
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null) {
            Log.d("MyService", "데이터베이스 오픈됨.");
        }
    }

    private void createTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer, time integer)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoInsert(String mac, String title, String contents, int date, int time) {
        if (database != null) {
            String sql = "INSERT INTO memo(mac, title, contents, date, time) VALUES(?,?,?,?,?)";
            Object[] params = {mac, title, contents, date, time};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoUpdate(String mac, String title, String contents, String time) {
        if (database != null) {
            String sql = "UPDATE memo SET mac = '" + mac + "', title = '" + title + "', contents = '" + contents + "' WHERE time = " + time;

            database.execSQL(sql);

            Log.d("MyService", "데이터 수정함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void macSelect(String macName) {
        if (database != null) {
            String sql = "Select mac, macName FROM mac WHERE macName = '" + macName + "'";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    mac = cursor.getString(cursor.getColumnIndex("mac"));
                }
            }
        }
    }
}
