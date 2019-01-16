package com.example.a82102.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class Checking_Wifi extends AppCompatActivity {
    public static Activity _Checking_Wifi;

    SQLiteDatabase database;
    ArrayList<String> items = new ArrayList<>();
    String mac;
    String title;
    String contents;
    String intent_date;
    int date;

    boolean update_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking__wifi);

        _Checking_Wifi = Checking_Wifi.this;

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        intent_date = intent.getStringExtra("intent_date");
        date = intent.getIntExtra("date", 0);
        update_check = intent.getBooleanExtra("update_check", false);

        openDatabase("databaseName");
        createTable();
        macSelect();
        final ListView listView = findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);

        Button button = findViewById(R.id.go_insertactivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Wifi_add.class);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
                intent.putExtra("intent_date",intent_date);
                intent.putExtra("date",date);
                intent.putExtra("update_check",update_check);

                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Memo memo = (Memo) Memo._memo;
                memo.finish();

                String macName = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), Memo.class);
                intent.putExtra("macName",macName);
                intent.putExtra("mac",mac);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
                intent.putExtra("intent_date",intent_date);
                intent.putExtra("date",date);
                intent.putExtra("update_check",update_check);
                startActivity(intent);

                finish();
            }
        });
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    private void createTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS mac(id integer PRIMARY KEY autoincrement, mac text, macName text)";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void macSelect() {
        if (database != null) {
            items.clear();
            String sql = "Select mac, macName FROM mac";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    mac = cursor.getString(cursor.getColumnIndex("mac"));
                    String macName = cursor.getString(cursor.getColumnIndex("macName"));
                    items.add(macName);
                }
            }
        }
    }
}
