package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import at.markushi.ui.CircleButton;

public class ChoiceWifi extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private SimpleAdapter simpleAdapter;
    public static ArrayList<HashMap<String,String>> list;
    public static ArrayList<String> arrayIndex;
    public static HashMap<String, String> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_wifi);
        mDbOpenHelper = new DbOpenHelper(getApplicationContext());

        listView();

        circleButton();


    }

    private void circleButton() {
        CircleButton circleButton = findViewById(R.id.wifi_Circle_Button);
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_add_intent = new Intent(getApplicationContext(), AddWifi.class);
                startActivityForResult(go_add_intent, 0);
            }
        });
    }

    private void select(boolean res) {
        Cursor iCursor = mDbOpenHelper.wifiSelectColumn();
        if (res) {
            list.clear();
            while(iCursor.moveToNext()) {
                item = new HashMap<>();
                item.put("item 1", iCursor.getString(iCursor.getColumnIndex("name")));
                item.put("item 2", iCursor.getString(iCursor.getColumnIndex("mac")));
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

    private void listView() {
        ListView listView = findViewById(R.id.wifi_listView);

        list = new ArrayList<>();
        arrayIndex = new ArrayList<>();
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        select(true);

        simpleAdapter = new SimpleAdapter(this, list, R.layout.list_item,
                new String[] {"item 1","item 2"},
                new int[] {R.id.text1, R.id.text2});
        listView.setAdapter(simpleAdapter);

        //짧게클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent go_memo_intent = new Intent();
                go_memo_intent.putExtra("name", list.get(position).get("item 1"));
                go_memo_intent.putExtra("mac", list.get(position).get("item 2"));
                setResult(1, go_memo_intent);
                finish();
            }
        });

        //길게클릭
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                select(false);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.RIGHT);
                final long nowIndex = Long.parseLong(arrayIndex.get(position));
                getMenuInflater().inflate(R.menu.list_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.list_menu1: //삭제
                                mDbOpenHelper.wifiDeleteColumn(nowIndex);
                                select(true);
                                simpleAdapter.notifyDataSetChanged();
                                break;
                            case R.id.list_menu2: //편집
                                Intent go_add_intent = new Intent(getApplicationContext(), AddWifi.class);
                                go_add_intent.putExtra("item 1", list.get(position).get("item 1"));
                                go_add_intent.putExtra("res", true);
                                go_add_intent.putExtra("nowIndex", nowIndex);
                                go_add_intent.putExtra("position", position);
                                startActivityForResult(go_add_intent, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case 1: //삽입
                String item1 = data.getStringExtra("item 1");
                String item2 = data.getStringExtra("item 2");
                item = new HashMap<>();
                item.put("item 1", item1);
                item.put("item 2", item2);
                list.add(item);
                simpleAdapter.notifyDataSetChanged();
                break;

            case 2: //편집
                String updateItem1 = data.getStringExtra("item 1");
                String updateItem2 = data.getStringExtra("item 2");
                int position = data.getIntExtra("position", 0);
                item = new HashMap<>();
                item.put("item 1", updateItem1);
                item.put("item 2", updateItem2);
                list.set(position, item);
                simpleAdapter.notifyDataSetChanged();
                break;
        }
    }
}
