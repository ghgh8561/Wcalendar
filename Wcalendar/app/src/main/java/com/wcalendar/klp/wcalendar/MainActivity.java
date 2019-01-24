package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView();

        listView();

        menu();

        circleButton();

    }

    private void circleButton() {
        CircleButton circleButton = findViewById(R.id.main_circle_Button);
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_memo_intent = new Intent(getApplicationContext(), Memo.class);
                startActivity(go_memo_intent);
            }
        });
    }

    private void menu() {
        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);

                getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu1:
                                Intent go_setting_intent = new Intent(getApplicationContext(), Setting.class);
                                startActivity(go_setting_intent);
                                break;
                            case R.id.menu2:
                                Toast.makeText(MainActivity.this, "메뉴2", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void listView() {

        ListView listView = findViewById(R.id.main_listView);
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item;
        item = new HashMap<String, String>();
        item.put("item 1", "제목 1");
        item.put("item 2", "내용 1");
        list.add(item);
        item = new HashMap<String, String>();
        item.put("item 1", "제목 2");
        item.put("item 2", "내용 2");
        list.add(item);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.list_item,
                new String[] {"item 1","item 2"},
                new int[] {R.id.text1, R.id.text2});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.RIGHT);

                getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.list_menu1:
                                Toast.makeText(MainActivity.this, "삭제", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.list_menu2:
                                Toast.makeText(MainActivity.this, "편집", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    public void CalendarView() {
        OneDayDecorator oneDayDecorator = new OneDayDecorator();

        MaterialCalendarView materialCalendarView = findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2038, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                oneDayDecorator);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                System.out.println("하이야" + date.getYear());

            }
        });

        //달력 빨간점 표시
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        ArrayList<CalendarDay> dates = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CalendarDay day = CalendarDay.from(calendar);
            dates.add(day);
            calendar.add(Calendar.DATE, 5);
        }

        materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));

    }
}
