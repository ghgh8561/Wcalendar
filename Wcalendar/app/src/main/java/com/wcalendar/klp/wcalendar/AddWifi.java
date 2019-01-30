package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddWifi extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);
        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        button();
    }

    private void button() {
        Button addWifi_insert_button = findViewById(R.id.addWifi_insert_button);
        Button addWifi_cancel_button = findViewById(R.id.addWifi_cancel_button);
        final EditText addWifi_wifiName = findViewById(R.id.addWifi_wifiName);
        WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        final String mac = info.getBSSID();


        //저장버튼
        addWifi_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addWifi_wifiName.getText().toString().trim();
                mDbOpenHelper.open();
                mDbOpenHelper.create();
                mDbOpenHelper.insertWifiColumn(mac, name);
                Intent go_choice_intent = new Intent();
                go_choice_intent.putExtra("item 1", name);
                go_choice_intent.putExtra("item 2", mac);
                setResult(0, go_choice_intent);
                finish();
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
