package com.wcalendar.klp.wcalendar;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.HashMap;

import at.markushi.ui.CircleButton;

public class ChoiceWifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_wifi);

        listView();

        circleButton();


    }

    private void circleButton() {
        CircleButton circleButton = findViewById(R.id.wifi_Circle_Button);
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_memo_intent = new Intent(getApplicationContext(), AddWifi.class);
                startActivity(go_memo_intent);
            }
        });
    }

    private void listView() {
        ListView listView = findViewById(R.id.wifi_listView);
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item;
        item = new HashMap<String, String>();
        item.put("item 1", "와이파이 이름 1");
        item.put("item 2", "와이파이 맥주소 1");
        list.add(item);
        item = new HashMap<String, String>();
        item.put("item 1", "와이파이 이름 2");
        item.put("item 2", "와이파이 맥주소 2");
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
                                Toast.makeText(ChoiceWifi.this, "삭제", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.list_menu2:
                                Toast.makeText(ChoiceWifi.this, "편집", Toast.LENGTH_SHORT).show();
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

}
