package com.wcalendar.klp.wcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        button();
    }

    private void button() {
        final Switch setting_rain_switch = findViewById(R.id.setting_rain_switch);
        Switch setting_dust_switch = findViewById(R.id.setting_dust_switch);
        final EditText setting_rain_editText = findViewById(R.id.setting_rain_editText);
        final LinearLayout setting_button_layout = findViewById(R.id.setting_button_layout);

        setting_rain_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setting_rain_switch.isChecked()) {
                    setting_rain_editText.setVisibility(View.VISIBLE);
                    setting_button_layout.setVisibility(View.VISIBLE);
                }
                else {
                    setting_rain_editText.setVisibility(View.INVISIBLE);
                    setting_button_layout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
