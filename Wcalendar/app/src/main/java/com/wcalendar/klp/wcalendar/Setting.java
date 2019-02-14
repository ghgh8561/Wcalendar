package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    String test;
    EditText setting_rain_editText;
    Switch setting_rain_switch;
    Switch setting_dust_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        button();
        rain_Load_shared_pref();
        dust_Load_shared_pref();
    }

    public void dust_Load_shared_pref(){
        SharedPreferences sharedPreferences = getSharedPreferences("rain and dust",0);
        boolean dust_checking = sharedPreferences.getBoolean("dust_check",false);
        setting_dust_switch.setChecked(dust_checking);
    }

    public void rain_Load_shared_pref(){
        SharedPreferences sharedPreferences = getSharedPreferences("rain and dust",0);
        String num = sharedPreferences.getString("rain_num","");
        boolean rain_checking = sharedPreferences.getBoolean("rain_check",false);
        setting_rain_switch.setChecked(rain_checking);
        setting_rain_editText.setText(num);
    }

    private void button() {
        setting_rain_switch = findViewById(R.id.setting_rain_switch);
        setting_dust_switch = findViewById(R.id.setting_dust_switch);
        setting_rain_editText = findViewById(R.id.setting_rain_editText);
        final LinearLayout setting_button_layout = findViewById(R.id.setting_button_layout);
        final Button saveBtn = findViewById(R.id.setting_insert_button);
        final Button CanCleBtn = findViewById(R.id.setting_cancel_button);

        setting_rain_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(setting_rain_switch.isChecked()) {
                    setting_rain_editText.setVisibility(View.VISIBLE);
                    setting_button_layout.setVisibility(View.VISIBLE);
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                test = setting_rain_editText.getText().toString().trim();
                                int test_num = Integer.parseInt(test);
                                if (test_num > 100) {
                                    Toast.makeText(Setting.this, "입력범위를 초과하였습니다. 재입력 해주세요", Toast.LENGTH_SHORT).show();
                                    setting_rain_editText.setText(null);
                                    return;
                                }
                                SharedPreferences sharedPreferences = getSharedPreferences("rain and dust",0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("rain_check",setting_rain_switch.isChecked());
                                editor.putString("rain_num",test);
                                Intent value_service = new Intent(getApplicationContext(), MyService.class);
                                value_service.putExtra("Rain_Switch", setting_rain_switch.isChecked());
                                startService(value_service);
                                editor.commit();
                                Toast.makeText(Setting.this, "입력한건 : " + test, Toast.LENGTH_SHORT).show();
                            }catch (NumberFormatException e){
                                Toast.makeText(Setting.this, "재입력하삼 ㅎㅎ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    CanCleBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setting_rain_editText.setText(null);
                        }
                    });
                }
                else {
                    setting_rain_editText.setVisibility(View.INVISIBLE);
                    setting_button_layout.setVisibility(View.INVISIBLE);
                }
            }
        });
        setting_dust_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("rain and dust",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dust_check",setting_dust_switch.isChecked());
                editor.commit();
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }
}
