package com.example.ho.myapplication;

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



public class testing extends AppCompatActivity {



    public static Activity _testing;
    SQLiteDatabase database;
    EditText editText_title;
    EditText editText_contents;
    String mac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _testing = testing.this;

        setContentView(R.layout.activity_testing);

        TextView showdate = findViewById(R.id.show_date);
        Button date_btn = findViewById(R.id.date_btn);
        Button wifi_btn = findViewById(R.id.wifi_picker);
        Intent i = getIntent();
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.wifiname);
        textView.setText(intent.getStringExtra("macName"));
        mac = intent.getStringExtra("mac");
        Log.d("year", String.valueOf(intent.getIntExtra("c_year", 0)));
        Log.d("month", String.valueOf(intent.getIntExtra("c_month", 0)));
        Log.d("day", String.valueOf(intent.getIntExtra("c_day", 0)));





        editText_title = findViewById(R.id.memo_title);
        editText_contents = findViewById(R.id.memo_contents);
        editText_title.setText(intent.getStringExtra("title"));
        editText_contents.setText(intent.getStringExtra("contents"));

        //Main2Activity 달력에서 선택한 일자
        String c_year = String.valueOf(i.getIntExtra("c_year", 0));
        String c_month = String.valueOf(i.getIntExtra("c_month", 0));
        String c_day = String.valueOf(i.getIntExtra("c_day", 0));
        String choose_date = c_year + "/" + c_month + "/" + c_day;
        showdate.setText(choose_date);

        //SpinnerActivity 스피너에서 선택한 일자
        String s_year = String.valueOf(intent.getIntExtra("s_year", 0));
        String s_month = String.valueOf(intent.getIntExtra("s_month", 0));
        String s_day = String.valueOf(intent.getIntExtra("s_day", 0));
        String spinner_date = s_year + "/" + s_month + "/" + s_day;
        showdate.setText(spinner_date);

        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SpinnerActivity.class);
                startActivity(intent);
            }
        });


         wifi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), wifi_listview.class);
                String title = editText_title.getText().toString().trim();
                String contents = editText_contents.getText().toString().trim();
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
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
                memoInsert(mac, title, contents);

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
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoInsert(String mac, String title, String contents) {
        if (database != null) {
            String sql = "INSERT INTO memo(mac, title, contents) VALUES(?,?,?)";
            Object[] params = {mac, title, contents};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }
}
