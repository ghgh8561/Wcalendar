package com.example.a82102.demo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class test extends AppCompatActivity {

    SQLiteDatabase database;
    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);


        Intent intent = new Intent(this, Background.class);
        startService(intent);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);

        openDatabase("databaseName");
        memoCreateTable();
        memoSelect();
        macCreateTable();
        macSelect();


        Button button1 = findViewById(R.id.drop_table1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (database != null) {
                    String sql = "DROP TABLE memo";
                    database.execSQL(sql);

                    Log.d("MyService", "테이블 삭제됨.");
                } else {
                    Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
                }
            }
        });

        Button button2 = findViewById(R.id.drop_table2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (database != null) {
                    String sql = "DROP TABLE mac";
                    database.execSQL(sql);

                    Log.d("MyService", "테이블 삭제됨.");
                } else {
                    Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
                }

            }
        });
//        Button button3 = findViewById(R.id.go_main2_button);
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                startActivity(intent);
//            }
//        });
    }


    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if (database != null) {
            Log.d("MyService", "데이터베이스 오픈됨.");
        }
    }

    private void memoSelect() {
        Log.d("MyService", "memoSelect() 호출됨.");

        if (database != null) {
            String sql = "SELECT mac, title, contents, date FROM memo";
            //String sql = "SELECT mac, title, contents FROM memo";
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String mac = cursor.getString(cursor.getColumnIndex("mac"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String contents = cursor.getString(cursor.getColumnIndex("contents"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                textView1.append(mac + " / " + title + " / " + contents + " / " + date + "\n");
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoCreateTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void macCreateTable() {
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
            String sql = "Select id, mac, macName FROM mac";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String mac = cursor.getString(cursor.getColumnIndex("mac"));
                    String macName = cursor.getString(cursor.getColumnIndex("macName"));
                    textView2.append(id + " / " + mac + " / " + macName + "\n");
                }
            }
        }
    }
}
