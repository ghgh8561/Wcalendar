package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class SpinnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spinner);



        DatePicker date_spinner = findViewById(R.id.date_spinner);

        Intent spinner_intent = getIntent();

        date_spinner.init(spinner_intent.getIntExtra("year", date_spinner.getYear()), spinner_intent.getIntExtra("month", date_spinner.getMonth()), spinner_intent.getIntExtra("day", date_spinner.getDayOfMonth()),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    final public void onDateChanged(DatePicker view, final int year, final int month, final int day) {
                        button(year, month+1, day);
                    }
        });
        button(date_spinner.getYear(), date_spinner.getMonth()+1, date_spinner.getDayOfMonth());
    }

    private void button(final int year, final int month, final int day) {
        Button btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_memo_intent = new Intent();
                go_memo_intent.putExtra("s_year",year);
                go_memo_intent.putExtra("s_month", month);
                go_memo_intent.putExtra("s_day",day);
                setResult(2, go_memo_intent);
                finish();
            }
        });
    }

}
