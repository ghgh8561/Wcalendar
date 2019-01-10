package com.example.ho.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyService extends Service {
    ConnectivityManager cm;
    SQLiteDatabase database;
    String mac_address;
    String title;
    String contents;

    String Weather_ServiceKey = "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D";
    String dust_ServiceKey = "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D";

    //미세먼지
    String timeStr; // 예보시간
    String areaStr; // 지역

    //기상
    String Weather_time; //날씨예보시간
    String POP; // 강수확률

    String dust;
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            public void onAvailable (Network network) {
                Log.d("MyService", "테스트 onAvailable");
                WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                mac_address = info.getBSSID();
                new dust_XMLparser().execute();
                new weather_XMLparser().execute();
            }
            public void onLost(Network network) {
                Log.d("MyService", "테스트 onLost");

                openDatabase("databaseName");

                createTable();

                memoSelect();

                new dust_XMLparser().weatherNotificationService();

                new weather_XMLparser().fall_NotificationService();
            }
        });
        return START_STICKY;
    }

    private void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);

        if (database != null) {
            Log.d("MyService", "데이터베이스 오픈됨.");
        }
    }

    private void createTable() {
        if (database != null) {
            String sql = "CREATE TABLE IF NOT EXISTS mac(id integer PRIMARY KEY autoincrement, mac text NOT NULL, macName text NOT NULL)";
            database.execSQL(sql);

            String sql2 = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text)";
            database.execSQL(sql2);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void notificationService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(contents);

        Intent intent = new Intent(this, MyService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
//             R.mipmap.ic_launcher);
//       builder.setLargeIcon(largeIcon);

        builder.setColor(Color.RED);

        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(ringtoneUri);

        long[] vibrate = {0, 100, 200, 300};
        builder.setVibrate(vibrate);
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        manager.notify(1, builder.build());
    }

    private void memoSelect() {
        Log.d("MyService", "memoSelect() 호출됨.");

        if (database != null) {
            String sql = "SELECT title, contents FROM memo WHERE mac = '" + mac_address + "'";
            //String sql = "SELECT mac, title, contents FROM memo";
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                title = cursor.getString(cursor.getColumnIndex("title"));
                contents = cursor.getString(cursor.getColumnIndex("contents"));
            }

            if(cursor.getCount() != 0) {
                notificationService();
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }
    public class weather_XMLparser extends  AsyncTask<String, Void, Document>{
        Document doc;
        @Override
        protected Document doInBackground(String... urls){
            URL url;
            try{
                StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + Weather_ServiceKey); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("TEST_SERVICE_KEY", "UTF-8")); /*서비스 인증*/
                urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20190110", "UTF-8")); /*‘19년 01월 10일발표*/
                urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0800", "UTF-8")); /*08시 발표 * 기술문서 참조*/
                urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("91", "UTF-8")); /*예보지점의 X 좌표값*/
                urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("76", "UTF-8")); /*예보지점의 Y 좌표값*/
                url = new URL(urlBuilder.toString());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                doc = documentBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            }catch(Exception e){
                e.printStackTrace();
            }
            return doc;
        }
        @Override
        protected void onPostExecute(Document doc){
            POP = "";
            Weather_time = "";
            NodeList nodeList = doc.getElementsByTagName("item");
            //for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(0);
            Element element = (Element) node;

            NodeList time = element.getElementsByTagName("baseTime");
            Weather_time += "예보시간 : " + time.item(0).getChildNodes().item(0).getNodeValue();

            NodeList fall_per = element.getElementsByTagName("fcstValue");
            POP += "강수확률 : " + fall_per.item(0).getChildNodes().item(0).getNodeValue() + "%";

            Log.d("TAG",POP);
            super.onPostExecute(doc);
        }
        private void fall_NotificationService() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("강수확률");
            builder.setContentText(POP);

            Intent intent = new Intent(getApplicationContext(), weather_XMLparser.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
//             R.mipmap.ic_launcher);
//       builder.setLargeIcon(largeIcon);

            builder.setColor(Color.RED);

            Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),
                    RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(ringtoneUri);

            long[] vibrate = {0, 100, 200, 300};
            builder.setVibrate(vibrate);
            builder.setAutoCancel(true);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                        NotificationManager.IMPORTANCE_DEFAULT));
            }
            manager.notify(3, builder.build());
        }
    }
    //미세먼지 파싱
    public class dust_XMLparser extends AsyncTask<String, Void, Document> {
        Document doc;
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureLIst"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + dust_ServiceKey); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("itemCode", "UTF-8") + "=" + URLEncoder.encode("PM10", "UTF-8")); /*측정항목 구분 (SO2, CO, O3, NO2, PM10, PM25)*/
                urlBuilder.append("&" + URLEncoder.encode("dataGubun", "UTF-8") + "=" + URLEncoder.encode("HOUR", "UTF-8")); /*요청 자료 구분 (시간평균 : HOUR, 일평균 : DAILY)*/
                urlBuilder.append("&" + URLEncoder.encode("searchCondition", "UTF-8") + "=" + URLEncoder.encode("MONTH", "UTF-8")); /*요청 데이터기간 (일주일 : WEEK, 한달 : MONTH)*/
                url = new URL(urlBuilder.toString());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                doc = documentBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            timeStr = "";
            areaStr = "";
            NodeList nodeList = doc.getElementsByTagName("item");
            //for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(0);
                Element element = (Element) node;

                NodeList datatime = element.getElementsByTagName("dataTime");
                timeStr += datatime.item(0).getChildNodes().item(0).getNodeValue() + "\n";

                NodeList area = element.getElementsByTagName("gyeongnam");
                dust = area.item(0).getChildNodes().item(0).getNodeValue();
                areaStr += "경남 미세먼지 = " + dust;
            //}
            super.onPostExecute(doc);
        }

        private void weatherNotificationService() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(timeStr + " " + areaStr);
            if(Integer.parseInt(dust)<=40)
                builder.setContentText("미세먼지 좋음");
            if(40<Integer.parseInt(dust) && Integer.parseInt(dust)<=80)
                builder.setContentText("미세먼지 보통");
            if(Integer.parseInt(dust)>80 && Integer.parseInt(dust)<=120)
                builder.setContentText("미세먼지 나쁨");
            if(Integer.parseInt(dust)>120)
                builder.setContentText("미세먼지 매우나쁨");

            Intent intent = new Intent(getApplicationContext(), dust_XMLparser.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
//             R.mipmap.ic_launcher);
//       builder.setLargeIcon(largeIcon);

            builder.setColor(Color.RED);

            Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),
                    RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(ringtoneUri);

            long[] vibrate = {0, 100, 200, 300};
            builder.setVibrate(vibrate);
            builder.setAutoCancel(true);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                        NotificationManager.IMPORTANCE_DEFAULT));
            }
            manager.notify(2, builder.build());
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
