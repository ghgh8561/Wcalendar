package com.example.ho.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase database;
    TextView textView1;
    TextView textView2;

    private int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callPermission();
        Intent intent = new Intent(this, MyService.class);
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

        Button button3 = findViewById(R.id.go_main2_button);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
            }
        });


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
            String sql = "SELECT mac, title, contents FROM memo";
            //String sql = "SELECT mac, title, contents FROM memo";
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String mac = cursor.getString(cursor.getColumnIndex("mac"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String contents = cursor.getString(cursor.getColumnIndex("contents"));
                textView1.append(mac + " / " + title + " / " + contents + "\n");
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoCreateTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text)";
            //String sql = "DROP table " + "memo";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void macCreateTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + "mac" + "(id integer PRIMARY KEY autoincrement, mac text NOT NULL, macName text NOT NULL)";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }
    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
}