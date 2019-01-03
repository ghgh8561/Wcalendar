package com.example.ho.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase database;
    ArrayList<String> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        Button create_btn = findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TableName = "Wifi_Table";
                openDatabase();
                createTable(TableName);
                insertData();
                lookupData(TableName);
            }
        });
    }

    public void openDatabase(){
        String DatabaseName = "wifi";
        database = openOrCreateDatabase(DatabaseName, MODE_PRIVATE, null);
        if(database != null){
            Log.d("TAG","db오픈");
        }
    }

    public void createTable(String TableName){
        if(database != null){
            String sql = "create Table if not exists " + TableName + "(_id integer PRIMARY KEY autoincrement,name Text)";
            database.execSQL(sql);
            Log.d("TAG","Table생성");
        }else
            Log.d("TAG","먼저 생성버튼눌러요");
    }
    public void insertData(){
        if(database != null){
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String name = wifiInfo.getSSID();
            String sql = "insert into WIFI_table(name) values(?)";
            Object[] objects = {name};
            database.execSQL(sql, objects);
            Log.d("TAG","데이터 삽입");
        }else
            Log.d("TAG","먼저 생성버튼눌러요");
    }
    public void lookupData(String TableName) {
        if (database != null) {
            items.clear();
            String sql = "select name from " + TableName;
            Cursor cursor = database.rawQuery(sql, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                items.add(name);
            }
            ListView listView = findViewById(R.id.wifilist);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, Collections.singletonList(items));
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }
}
