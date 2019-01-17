package com.example.a82102.demo;

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
import android.os.Binder;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Background extends Service {
    Boolean fall_check;
    Boolean dust_check;

    public Background() {
    }

    ConnectivityManager cm;
    SQLiteDatabase database;
    String mac_address;
    String title;
    String contents;

    String dust_ServiceKey = "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D";

    String timeStr;
    String areaStr;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        dust_check = intent.getBooleanExtra("dust_check", false);

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
                new XMLparser().execute();

            }
            public void onLost(Network network) {
                Log.d("MyService", "테스트 onLost");

                openDatabase("databaseName");

                createTable();

                memoSelect();

                if (getDust_check() == true) {
                    new XMLparser().weatherNotificationService();
                }

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
            String sql = "CREATE TABLE IF NOT EXISTS mac(id integer PRIMARY KEY autoincrement, mac text, macName text)";
            database.execSQL(sql);

            String sql2 = "CREATE TABLE IF NOT EXISTS memo(mac text, title text, contents text, date integer, time integer)";
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

        Intent intent = new Intent(this, Background.class);
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

        SimpleDateFormat df_date = new SimpleDateFormat("yyyyMd", Locale.KOREA);
        int date = Integer.parseInt(df_date.format(new Date()));
        if (database != null) {
            String sql = "SELECT title, contents "
                       + "FROM memo "
                       + "WHERE mac = '" + mac_address + "' "
                       + "AND date = " + "'" + date + "'";
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

    public class XMLparser extends AsyncTask<String, Void, Document> {
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
            areaStr += "경남 미세먼지 = " + area.item(0).getChildNodes().item(0).getNodeValue() + "\n";
            //}
            super.onPostExecute(doc);
        }

        private void weatherNotificationService() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(timeStr + " " + areaStr);
            builder.setContentText("미세먼지 좋음");

            Intent intent = new Intent(getApplicationContext(), XMLparser.class);
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

    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        Background getService() { // 서비스 객체를 리턴
            return Background.this;
        }
    }

    public Boolean getFall_check() {
        return fall_check;
    }

    public void setFall_check(Boolean fall_check) {
        this.fall_check = fall_check;
    }

    public Boolean getDust_check() {
        return dust_check;
    }

    public void setDust_check(Boolean fall_check) {
        this.dust_check = dust_check;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
}

