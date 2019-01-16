package com.example.ho.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

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

        Button wifi_btn = findViewById(R.id.wifi_picker);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.wifiname);
        textView.setText(intent.getStringExtra("macName"));
        mac = intent.getStringExtra("mac");
        Log.d("year", String.valueOf(intent.getIntExtra("year", 0)));
        Log.d("month", String.valueOf(intent.getIntExtra("month", 0)));
        Log.d("day", String.valueOf(intent.getIntExtra("day", 0)));


        editText_title = findViewById(R.id.memo_title);
        editText_contents = findViewById(R.id.memo_contents);
        editText_title.setText(intent.getStringExtra("title"));
        editText_contents.setText(intent.getStringExtra("contents"));

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

        Spinner yspinner = (Spinner)findViewById(R.id.spinner_year);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.year, android.R.layout.simple_spinner_item);
        yspinner.setAdapter(yearAdapter);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window = (ListPopupWindow)popup.get(yspinner);
            window.setHeight(500); //pixel
        } catch (Exception e) {
            e.printStackTrace();
        }


        Spinner mspinner = (Spinner)findViewById(R.id.spinner_month);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.month, android.R.layout.simple_spinner_item);
        mspinner.setAdapter(monthAdapter);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window = (ListPopupWindow)popup.get(mspinner);
            window.setHeight(400); //pixel
        } catch (Exception e) {
            e.printStackTrace();
        }

        Spinner dspinner = (Spinner)findViewById(R.id.spinner_day);
        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this, R.array.day, android.R.layout.simple_spinner_item);
        dspinner.setAdapter(dayAdapter);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window = (ListPopupWindow)popup.get(dspinner);
            window.setHeight(400); //pixel
        } catch (Exception e) {
            e.printStackTrace();
        }


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
