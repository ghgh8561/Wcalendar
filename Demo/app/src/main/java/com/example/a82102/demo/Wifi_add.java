package com.example.a82102.demo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Wifi_add extends AppCompatActivity {
    SQLiteDatabase database;
    WifiManager mng;
    WifiInfo info;
    String mac;
    EditText editText;
    String title;
    String contents;
    String intent_date;
    int date;

    boolean update_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_add);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        contents = intent.getStringExtra("contents");
        intent_date = intent.getStringExtra("intent_date");
        date = intent.getIntExtra("date", 0);
        update_check = intent.getBooleanExtra("update_check", false);

        openDatabase("databaseName");
        createTable();

        mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        info = mng.getConnectionInfo();
        mac = info.getBSSID();

        editText = findViewById(R.id.wifiname_editText);
        Button button = findViewById(R.id.wifiname_insert_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                        info = mng.getConnectionInfo();
                        mac = info.getBSSID();

                        String macName = editText.getText().toString().trim();
                        insertData(mac, macName);

                        Checking_Wifi wl = (Checking_Wifi) Checking_Wifi._Checking_Wifi;
                        wl.finish();

                        Intent intent = new Intent(getApplicationContext(), Checking_Wifi.class);
                        intent.putExtra("title",title);
                        intent.putExtra("contents",contents);
                        intent.putExtra("intent_date",intent_date);
                        intent.putExtra("date",date);
                        intent.putExtra("update_check",update_check);
                        startActivity(intent);

                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "와이파이 연결하세요", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "연결 안됨", Toast.LENGTH_SHORT).show();
                }
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
            String sql = "CREATE TABLE IF NOT EXISTS mac(id integer PRIMARY KEY autoincrement, mac text, macName text)";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void insertData(String mac, String macName) {
        if (database != null) {
            String sql = "INSERT INTO mac(mac, macName) VALUES(?,?)";
            Object[] params = {mac, macName};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }
}
