package com.example.ho.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    SQLiteDatabase database;
    WifiManager mng;
    WifiInfo info;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        String databaseName = "databaseName";
        openDatabase(databaseName);

        Button button = (Button)findViewById(R.id.db_create_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = "tableName";
                createTable(tableName);
            }
        });

        Button button2 = (Button)findViewById(R.id.db_insert_button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                info = mng.getConnectionInfo();
                mac = info.getBSSID();

                insertData(mac);
            }
        });

        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (database != null) {
                    String sql = "DROP table " + "tableName";
                    database.execSQL(sql);

                    String sql2 = "DROP table " + "memo";
                    database.execSQL(sql2);

                    Log.d("MyService", "테이블 삭제됨.");
                } else {
                    Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
                }
            }
        });

        Button button4 = (Button)findViewById(R.id.db_selectdata_button);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = "tableName";
                selectData(tableName);
            }
        });

        editText = (EditText)findViewById(R.id.EditText);
        Button button5 = (Button)findViewById(R.id.db_memo_button);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = "memo";
                memo(tableName);
                mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                info = mng.getConnectionInfo();
                mac = info.getBSSID();
                String title = editText.getText().toString().trim();
                memoInsert(mac, title);
            }
        });

        Button button6 = findViewById(R.id.go_main2_button);
        button6.setOnClickListener(new View.OnClickListener() {
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

    private void createTable(String tableName) {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(_id integer PRIMARY KEY autoincrement, mac text)";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void insertData(String mac) {
        Log.d("MyService", "insertData() 호출됨.");

        if (database != null) {
            String sql = "insert into tableName(mac) values(?)";
            Object[] params = {mac};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void selectData(String tableName) {
        Log.d("MyService", "selectData() 호출됨.");

        if (database != null) {
            String sql = "select mac from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String mac = cursor.getString(0);

                Log.d("MyService", "#" + i + "->" + mac);
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memo(String tableName) {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(mac text, title text)";
            database.execSQL(sql);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void memoInsert(String mac, String title) {
        if (database != null) {
            String sql = "insert into memo(mac, title) values(?,?)";
            Object[] params = {mac, title};

            database.execSQL(sql, params);

            Log.d("MyService", "데이터 추가함.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

}
