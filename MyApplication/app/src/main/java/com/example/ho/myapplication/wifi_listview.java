package com.example.ho.myapplication;

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

public class wifi_listview extends AppCompatActivity{

    SQLiteDatabase database;
    ArrayList<String> items = new ArrayList<>();
    String mac;
    String title;
    String contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_listview);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");

        openDatabase("databaseName");
        createTable();
        lookupdata("mac");
        final ListView listView = findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);

        Button button = findViewById(R.id.go_insertactivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), insert_db.class);
                startActivity(intent);

                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String macName = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), testing.class);
                intent.putExtra("macName",macName);
                intent.putExtra("mac",mac);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);
                startActivity(intent);

                testing te = (testing)testing._testing;
                te.finish();

                finish();
            }
        });
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    private void createTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + "mac" + "(id integer PRIMARY KEY autoincrement, mac text NOT NULL, macName text NOT NULL)";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    public void lookupdata(String tablename) {
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