package com.example.a82102.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Sub_Set extends AppCompatActivity {

    CheckBox fall;
    CheckBox dust;

    Boolean fall_check;
    Boolean dust_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub__set);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        fall = findViewById(R.id.fall);
        dust = findViewById(R.id.dust);
        Boolean fall_chk = pref.getBoolean("fall", false);
        Boolean dust_chk = pref.getBoolean("dust", false);

        fall.setChecked(fall_chk);
        dust.setChecked(dust_chk);

        final Intent intent = new Intent(getApplicationContext(),Background.class);

        final Background ms = new Background();

        dust.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(dust.isChecked()) {
                    dust_check = true;
                    intent.putExtra("dust_check", true);
                    ms.setDust_check(dust_check);
                    startService(intent);
                } else if(!dust.isChecked()) {
                    dust_check = false;
                    intent.putExtra("dust_check", false);
                    ms.setDust_check(dust_check);
                    startService(intent);
                }
            }
        });


    }

    public void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        fall = findViewById(R.id.fall);
        dust = findViewById(R.id.dust);

        editor.putBoolean("fall", fall.isChecked());
        editor.putBoolean("dust", dust.isChecked());

        Intent intent = new Intent(getApplicationContext(),Background.class);

        Background ms = new Background();

        if(dust.isChecked()) {
            dust_check = true;
            intent.putExtra("dust_check", true);
            ms.setDust_check(dust_check);
            startService(intent);
        }
        else if(!dust.isChecked()) {
            dust_check = false;
            intent.putExtra("dust_check", false);
            ms.setDust_check(dust_check);
            startService(intent);
        }

        editor.commit();
    }
}
