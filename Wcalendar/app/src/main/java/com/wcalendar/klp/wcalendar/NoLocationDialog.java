package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class NoLocationDialog extends AppCompatActivity {

    private Button MoveBtn;
    private Button CloseBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_no_location_dialog);

        button_();
    }

    public void button_(){
        MoveBtn = findViewById(R.id.M_btn);
        CloseBtn = findViewById(R.id.C_btn);

        MoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
                System.out.print("이동해임마");
            }
        });
        CloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("종료");
                finish();
            }
        });
    }
}
