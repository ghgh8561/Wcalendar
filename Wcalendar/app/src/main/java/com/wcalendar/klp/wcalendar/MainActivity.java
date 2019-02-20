package com.wcalendar.klp.wcalendar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private SimpleAdapter simpleAdapter;
    public static ArrayList<HashMap<String,String>> list;
    public static ArrayList<String> arrayIndex;
    public static HashMap<String, String> item;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        CalendarView();

        //listView();

        menu();

        permissioncheck();
    }

    private void permissioncheck(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() { // 퍼미션 권한 획득시 실행되는 메소드
                Intent Service_Start_intent = new Intent(getApplicationContext(),MyService.class);
                Service_State service_state = new Service_State(getApplicationContext());
                if(!service_state.isServiceRunning())
                    startService(Service_Start_intent);
                Toast.makeText(getApplicationContext(),"됏다!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) { // 퍼미션 권한 거부시 실행되는 메소드
                Toast.makeText(getApplicationContext(),"안됏네 ㅠㅠ!",Toast.LENGTH_LONG).show();
            }
        };
        //거부누를시 설정창으로 이동하게 되는부분
        TedPermission.with(this).setPermissionListener(permissionListener).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void circleButton(final int year, final int month, final int day) {
        CircleButton circleButton = findViewById(R.id.main_circle_Button);
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_memo_intent = new Intent(getApplicationContext(), Memo.class);
                go_memo_intent.putExtra("intent_year", year);
                go_memo_intent.putExtra("intent_month", month);
                go_memo_intent.putExtra("intent_day", day);
                startActivityForResult(go_memo_intent, 0);
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

    private void select(boolean res, int year, int month, int day) {
        Cursor iCursor = mDbOpenHelper.memoSelectColumn(year, month, day);
        if (res) {
            list.clear();
            while(iCursor.moveToNext()) {
                item = new HashMap<>();
                item.put("item 1", iCursor.getString(iCursor.getColumnIndex("title")));
                item.put("item 2", iCursor.getString(iCursor.getColumnIndex("contents")));
                item.put("item 3", iCursor.getString(iCursor.getColumnIndex("mac")));
                list.add(item);
                arrayIndex.add(iCursor.getString(iCursor.getColumnIndex("_id")));
            }
        }
        else {
            arrayIndex.clear();
            while(iCursor.moveToNext()) {
                arrayIndex.add(iCursor.getString(iCursor.getColumnIndex("_id")));
            }
        }
    }

    private void listView(final int year, final int month, final int day) {
        ListView listView = findViewById(R.id.main_listView);

        list = new ArrayList<>();
        arrayIndex = new ArrayList<>();
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        select(true, year, month, day);

        simpleAdapter = new SimpleAdapter(this, list, R.layout.list_item,
                new String[] {"item 1","item 2"},
                new int[] {R.id.text1, R.id.text2});
        listView.setAdapter(simpleAdapter);

        //짧게 클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                select(false, year, month, day);
                long nowIndex = Long.parseLong(arrayIndex.get(position));
                Intent go_memo_intent = new Intent(getApplicationContext(), Memo.class);
                go_memo_intent.putExtra("item 1", list.get(position).get("item 1"));
                go_memo_intent.putExtra("item 2", list.get(position).get("item 2"));
                go_memo_intent.putExtra("item 3", list.get(position).get("item 3"));
                go_memo_intent.putExtra("year", year);
                go_memo_intent.putExtra("month", month);
                go_memo_intent.putExtra("day", day);
                go_memo_intent.putExtra("res", true);
                go_memo_intent.putExtra("nowIndex", nowIndex);
                go_memo_intent.putExtra("position", position);
                startActivityForResult(go_memo_intent, 0);
            }
        });

        //길게 클릭
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                select(false, year, month, day);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.RIGHT);
                final long nowIndex = Long.parseLong(arrayIndex.get(position));
                getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.list_menu1: //삭제
                                mDbOpenHelper.memoDeleteColumn(nowIndex);
                                select(true, year, month, day);
                                simpleAdapter.notifyDataSetChanged();
                                CalendarView();
                                MaterialCalendarView materialCalendarView = findViewById(R.id.calendarView);
                                materialCalendarView.removeDecorators();
                                CalendarView();
                                break;

                            case R.id.list_menu2: //편집
                                Intent go_memo_intent = new Intent(getApplicationContext(), Memo.class);
                                go_memo_intent.putExtra("item 1", list.get(position).get("item 1"));
                                go_memo_intent.putExtra("item 2", list.get(position).get("item 2"));
                                go_memo_intent.putExtra("item 3", list.get(position).get("item 3"));
                                go_memo_intent.putExtra("year", year);
                                go_memo_intent.putExtra("month", month);
                                go_memo_intent.putExtra("day", day);
                                go_memo_intent.putExtra("res", true);
                                go_memo_intent.putExtra("nowIndex", nowIndex);
                                go_memo_intent.putExtra("position", position);
                                startActivityForResult(go_memo_intent, 0);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
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
                int year = date.getYear();
                int month = date.getMonth() + 1;
                int day = date.getDay();

                circleButton(year, month, day);

                listView(year, month, day);
            }
        });

        listView(CalendarDay.today().getYear(), CalendarDay.today().getMonth() + 1, CalendarDay.today().getDay());

        Calendar calendar = Calendar.getInstance();
        materialCalendarView.setDateSelected(calendar.getTime(), true);
        circleButton(CalendarDay.today().getYear(), CalendarDay.today().getMonth() + 1, CalendarDay.today().getDay());

        //달력 빨간점 표시
        Cursor iCursor = mDbOpenHelper.memoSelectColumn2();
        ArrayList<CalendarDay> dates = new ArrayList<>();
        while(iCursor.moveToNext()) {
            int year = Integer.parseInt(iCursor.getString(iCursor.getColumnIndex("year")));
            int month = Integer.parseInt(iCursor.getString(iCursor.getColumnIndex("month")));
            int dayy = Integer.parseInt(iCursor.getString(iCursor.getColumnIndex("day")));

            System.out.println("ggg " + year + "/" + month + "/" + dayy);
            calendar.set(year, month-1, dayy);
            CalendarDay day = CalendarDay.from(calendar);

            dates.add(day);
        }

        materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 1:
                String item1 = data.getStringExtra("item 1");
                String item2 = data.getStringExtra("item 2");
                String item3 = data.getStringExtra("item 3");
                item = new HashMap<>();
                item.put("item 1", item1);
                item.put("item 2", item2);
                item.put("item 3", item3);
                list.add(item);
                simpleAdapter.notifyDataSetChanged();
                CalendarView();
                break;

            case 2: //편집
                String updateItem1 = data.getStringExtra("item 1");
                String updateItem2 = data.getStringExtra("item 2");
                String updateItem3 = data.getStringExtra("item 3");
                int position = data.getIntExtra("position", 0);
                item = new HashMap<>();
                item.put("item 1", updateItem1);
                item.put("item 2", updateItem2);
                item.put("item 3", updateItem3);
                list.set(position, item);
                simpleAdapter.notifyDataSetChanged();
                CalendarView();
                break;
        }
    }
}
