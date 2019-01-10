package com.example.a82102.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Memo extends AppCompatActivity {
    public static Activity _memo;
    SQLiteDatabase database;
    EditText editText_title;
    EditText editText_contents;
    TextView date_textview;
    String mac;
    int date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _memo = Memo.this;

        setContentView(R.layout.activity_memo);

        Button wifi_btn = findViewById(R.id.wifi);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.wifiname);
        textView.setText(intent.getStringExtra("macName"));
        mac = intent.getStringExtra("mac");
        date_textview = findViewById(R.id.date_textView);
        date_textview.setText(intent.getStringExtra("intent_date"));
        date = Integer.parseInt(String.valueOf(intent.getIntExtra("year", 0)) + String.valueOf(intent.getIntExtra("month", 0)) + String.valueOf(intent.getIntExtra("day", 0)));

        System.out.println("하이" + date);
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
                startActivity(intent);
            }
        });

        openDatabase("databaseName");

        Button insert_db_button = findViewById(R.id.insert_db_button);
        insert_db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editText_title.getText().toString().trim();
                String contents = editText_contents.getText().toString().trim();
                createTable();
                memoInsert(mac, title, contents, date);

                finish();
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
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoInsert(String mac, String title, String contents, int date) {
        if (database != null) {
            String sql = "INSERT INTO memo(mac, title, contents, date) VALUES(?,?,?,?)";
            Object[] params = {mac, title, contents, date};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }
}
