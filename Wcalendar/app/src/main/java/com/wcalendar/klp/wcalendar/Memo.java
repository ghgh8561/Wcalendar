package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Memo extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    TextView memo_wifi_name;
    TextView memo_date;
    EditText memo_title;
    EditText memo_contents;
    long nowIndex;
    int year;
    int month;
    int day;
    int position;
    String mac;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        mDbOpenHelper = new DbOpenHelper(getApplicationContext());
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        layout();
    }

    private void layout() {
        memo_date = findViewById(R.id.memo_date);
        memo_wifi_name = findViewById(R.id.memo_wifi_name);
        memo_title = findViewById(R.id.memo_title);
        memo_contents = findViewById(R.id.memo_contents);

        Intent Memo_intent = getIntent();
        year = Memo_intent.getIntExtra("intent_year", 0);
        month = Memo_intent.getIntExtra("intent_month", 0);
        day = Memo_intent.getIntExtra("intent_day", 0);

        boolean res = Memo_intent.getBooleanExtra("res", false);

        if(res) { //편집
            mac = Memo_intent.getStringExtra("item 3");
            nowIndex = Memo_intent.getLongExtra("nowIndex", 0);
            position = Memo_intent.getIntExtra("position", 0);

            memo_title.setText(Memo_intent.getStringExtra("item 1"));
            memo_date.setText(String.valueOf(Memo_intent.getIntExtra("year", 0)));
            memo_date.append(String.valueOf(Memo_intent.getIntExtra("month", 0)));
            memo_date.append(String.valueOf(Memo_intent.getIntExtra("day", 0)));
            memo_contents.setText(Memo_intent.getStringExtra("item 2"));
            Cursor iCursor = mDbOpenHelper.wifiSelectColumn2(mac);
            while (iCursor.moveToNext()) {
                name = iCursor.getString(iCursor.getColumnIndex("name"));
            }
            memo_wifi_name.setText(name);
            button(true);
        }
        else { //삽입
            memo_date.append(String.valueOf(year) + String.valueOf(month) + String.valueOf(day));
            button(false);
        }


    }

    private void button(final boolean isUpdate) {
        Button memo_insert_button = findViewById(R.id.memo_insert_button);
        Button memo_cancel_button = findViewById(R.id.memo_cancel_button);
        ImageButton memo_wifi_imageButton = findViewById(R.id.memo_wifi_imageButton);
        ImageView memo_date_imageView = findViewById(R.id.memo_date_imageView);

        //저장버튼
        memo_insert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = memo_title.getText().toString().trim();
                String contents = memo_contents.getText().toString().trim();
                if(isUpdate) {
                    mDbOpenHelper.memoUpdateColums(nowIndex, title, contents, mac);
                    Intent go_main_intent = new Intent();
                    go_main_intent.putExtra("item 1", title);
                    go_main_intent.putExtra("item 2", contents);
                    go_main_intent.putExtra("item 3", mac);
                    go_main_intent.putExtra("position", position);
                    setResult(2, go_main_intent);
                    finish();
                }
                else {
                    mDbOpenHelper.insertMemoColumn(mac, title, contents, String.valueOf(year), String.valueOf(month), String.valueOf(day));
                    Intent go_main_intent = new Intent();
                    go_main_intent.putExtra("item 1", title);
                    go_main_intent.putExtra("item 2", contents);
                    go_main_intent.putExtra("item 3", mac);
                    setResult(1, go_main_intent);
                    finish();
                }
            }
        });

        //취소버튼
        memo_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //공유기버튼
        memo_wifi_imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_choice_wifi = new Intent(getApplicationContext(), ChoiceWifi.class);
                startActivityForResult(go_choice_wifi, 0);
            }
        });

        //날짜버튼
        memo_date_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_spinner_intent = new Intent(getApplicationContext(),SpinnerActivity.class);
                go_spinner_intent.putExtra("year", year);
                go_spinner_intent.putExtra("month", month-1); //spinner에서 +1로 받아짐
                go_spinner_intent.putExtra("day", day);
                startActivityForResult(go_spinner_intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 1: //wifi에서 받아오는 값
                String item1 = data.getStringExtra("name");
                mac = data.getStringExtra("mac");
                memo_wifi_name.setText(item1);
                break;
            case 2: //spinner에서 받아오는 값
                year = data.getIntExtra("s_year", 0);
                month = data.getIntExtra("s_month", 0);
                day = data.getIntExtra("s_day", 0);
                memo_date.setText("");
                memo_date.append(String.valueOf(year) + String.valueOf(month) + String.valueOf(day));
                break;
        }
    }
}
