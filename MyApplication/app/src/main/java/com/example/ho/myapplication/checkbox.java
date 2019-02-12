package com.example.ho.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class checkbox extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox);

        final CheckBox rain_setting_chkbox = findViewById(R.id.rain_setting_chkbox);
        final CheckBox dust_setting_chkbox = findViewById(R.id.dust_setting_chkbox);
        final LinearLayout rain_visible_box = findViewById(R.id.rain_visible_box);
        final LinearLayout dust_visible_box = findViewById(R.id.dust_visible_box);
        final TextView dust_setting_info = findViewById(R.id.dust_setting_info);
        final EditText dust_setting_number = findViewById(R.id.dust_setting_number);

        
        rain_setting_chkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rain_setting_chkbox.isChecked()) {
                    rain_visible_box.setVisibility(View.VISIBLE);
                }
                else {
                    rain_visible_box.setVisibility(View.GONE);
                }
            }
        });

        dust_setting_chkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dust_setting_chkbox.isChecked()) {
                    dust_visible_box.setVisibility(View.VISIBLE);
                }
                else {
                    dust_visible_box.setVisibility(View.GONE);
                }
            }
        });
    }
}