package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWifi extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private Intent add_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);
        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        add_intent = getIntent();
        boolean res = add_intent.getBooleanExtra("res", false);


        if(res) { //편집
            update();
        }
        else { //삽입
            button(false);
        }
    }

    private void update() {
        EditText addWifi_wifiName = findViewById(R.id.addWifi_wifiName);
        addWifi_wifiName.setText(add_intent.getStringExtra("item 1"));

        button(true);
    }

    private void button(final boolean isUpdate) {
        Button addWifi_insert_button = findViewById(R.id.addWifi_insert_button);
        final Button addWifi_cancel_button = findViewById(R.id.addWifi_cancel_button);
        final EditText addWifi_wifiName = findViewById(R.id.addWifi_wifiName);
        WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        final String mac = info.getBSSID();

        //저장버튼
        addWifi_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mac != null) {
                    String name = addWifi_wifiName.getText().toString().trim();
                    mDbOpenHelper.open();
                    mDbOpenHelper.create();

                    if(isUpdate) { //편집
                        long nowIndex = add_intent.getLongExtra("nowIndex", 0);
                        int position = add_intent.getIntExtra("position", 0);
                        mDbOpenHelper.wifiUpdateColumn(nowIndex, mac, name);
                        Intent go_choice_intent = new Intent();
                        go_choice_intent.putExtra("item 1", name);
                        go_choice_intent.putExtra("item 2", mac);
                        go_choice_intent.putExtra("position", position);
                        setResult(2, go_choice_intent);
                        finish();
                    }
                    else { //삽입
                        mDbOpenHelper.insertWifiColumn(mac, name);
                        Intent go_choice_intent = new Intent();
                        go_choice_intent.putExtra("item 1", name);
                        go_choice_intent.putExtra("item 2", mac);
                        setResult(1, go_choice_intent);
                        finish();
                    }
                }
                else {
                    Toast.makeText(AddWifi.this, "wifi 연결 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //취소버튼
        addWifi_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
