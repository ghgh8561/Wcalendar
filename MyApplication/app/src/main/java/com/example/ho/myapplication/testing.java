package com.example.ho.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
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
    private TextView showdate;
    private Button datepicker;
    private DatePickerDialog.OnDateSetListener dateSetListener;

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

        showdate = (TextView) findViewById(R.id.show_date);
        datepicker = (Button) findViewById(R.id.date_picker);
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

        String year = String.valueOf(intent.getIntExtra("year", 0));
        String month = String.valueOf(intent.getIntExtra("month", 0));
        String day = String.valueOf(intent.getIntExtra("day", 0));
        String choose_date = year + "/" + month + "/" + day;
        showdate.setText(choose_date);

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        testing.this, android.R.style.,
                        dateSetListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "/" + month + "/" + dayOfMonth;
                showdate.setText(date);
            }
        };

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
