package com.example.ho.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class checkbox extends AppCompatActivity {

    private final String TAG = "이건뭐 ㅡㅡ";
    private CheckBox fall;
    private CheckBox dust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //url부분
                    StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst");//url 사이트
                    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D");//인증키
                    urlBuilder.append("&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + URLEncoder.encode("경남", "UTF-8"));//시도이름
                    urlBuilder.append("&" + URLEncoder.encode("serchCondition", "UTF-8") + "=" + URLEncoder.encode("HOUR", "UTF-8")); // DAILY 하루 HOUR 시간

                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    Log.d("TAG", "Response Code:" + conn.getResponseCode());
                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    StringBuilder SB = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        SB.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                    Log.d("TAG", SB.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("TAG", "에러발생햇쩌염");
                }
            }
        });
    }
}
