package com.example.a82102.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Sub_Set extends AppCompatActivity {

    CheckBox fall;
    CheckBox dust;

    Boolean fall_check;
    Boolean dust_check;

    //퍼미션 확인
    private int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission;

    Background ms = new Background();

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
                    if(!isPermission){
                        callPermission();
                    }
                    dust_check = true;
                    intent.putExtra("dust_check", true);
                    ms.setDust_check(dust_check);
                    intent.putExtra("isPermission", isPermission);
                    startService(intent);
                } else if(!dust.isChecked()) {
                    dust_check = false;
                    intent.putExtra("dust_check", false);
                    ms.setDust_check(dust_check);
                    startService(intent);
                }
            }
        });
        fall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fall.isChecked()){
                    if(!isPermission){
                        callPermission();
                    }
                    fall_check = true;
                    intent.putExtra("fall_check",true);
                    ms.setFall_check(fall_check);
                    intent.putExtra("isPermission",isPermission);
                    startService(intent);
                }
                if(!fall.isChecked()){
                    fall_check = false;
                    intent.putExtra("fall_check",false);
                    ms.setFall_check(fall_check);
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
        editor.putBoolean("isPermission", isPermission);

        Intent intent = new Intent(getApplicationContext(),Background.class);


        if(isPermission){
            intent.putExtra("isPermission",isPermission);
            ms.setPermission_check(isPermission);
            startService(intent);
        }
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
        if(fall.isChecked()){
            fall_check = true;
            intent.putExtra("fall_check", true);
            ms.setFall_check(fall_check);
            startService(intent);
        }
        if(!fall.isChecked()){
            fall_check = false;
            intent.putExtra("fall_check", false);
            ms.setFall_check(fall_check);
            startService(intent);
        }
        editor.commit();
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
    // 위치 권한 요청
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
        }else{
            isPermission = true;
        }
    }
}
