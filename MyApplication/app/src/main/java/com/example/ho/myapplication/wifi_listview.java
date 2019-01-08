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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_listview);

        String tableName = "tableName";
        opendatabase();
        lookupdata(tableName);
        final ListView listView = findViewById(R.id.listview);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);

        Button button = findViewById(R.id.go_insertactivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), insert_db.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mac = (String) listView.getItemAtPosition(position);
                Log.d("TAG", mac);
                Intent intent = new Intent(getApplicationContext(), testing.class);
                intent.putExtra("mac",mac);
                startActivity(intent);
            }
        });
    }

    private void opendatabase() {
        String databasename = "databaseName";
        database = openOrCreateDatabase(databasename, MODE_PRIVATE, null);
    }

    public void lookupdata(String tablename) {
        if (database != null) {
            items.clear();
            String sql = "Select mac FROM " + tablename;
            Cursor cursor = database.rawQuery(sql, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndex("mac"));
                items.add(name);
            }
        }
    }
}