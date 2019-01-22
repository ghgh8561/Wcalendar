package com.example.a82102.demo;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Background extends Service implements LocationListener {
    Boolean fall_check;
    Boolean dust_check;
    Boolean permission_check;

    public Background() {
    }

    ConnectivityManager cm;
    SQLiteDatabase database;
    String mac_address;
    String title;
    String contents;
    String Year_Month_Day; // 년월일
    String Timer; // 발표시간
    int fcstTime; // 예보시간
    String POP; // 강수확률
    String Weather_time; //날씨예보시간
    String temp; //현재기온(3시간기준)
    String wfKor; //현재날씨상태

    String dust_ServiceKey = "=avRyiVbF2TLqxBhIM2k5I%2B1ftisPzEeqdoqkmchNU0eZh48XElEJPsmtqp8oT2%2BPycIvIoMXeEehXtxwJVL1ow%3D%3D";

    // 미세먼지 파싱 변수
    String timeStr; // 시간
    String areaStr; // 지역
    String dust; // 미세먼지
    String cho_dust; // 초미세먼지
    String address; // 지역(도)
    String[] sigoongo;

    // 현재 디바이스 위치 위경도 알아오기
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
    // 네트워크 사용유무
    boolean isNetworkEnabled = false;
    // GPS 상태값
    boolean isGetLocation = false;
    Location location;
    double lat; // 위도
    double lon; // 경도
    // 최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    List<Address> address_List = null; // 위경도기준 주소

    public Location getLocation() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return null;
        }

        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(Background.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     * */
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     * */
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void MyLocation(){
        getLocation();
        if (isGetLocation()) {
            lat = getLatitude();
            lon = getLongitude();
        }
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getApplicationContext().startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        permission_check = intent.getBooleanExtra("isPermission",false);
        Log.d("permission : ", String.valueOf(permission_check));
        if(getPermission_check()) {
            MyLocation();
        }

        dust_check = intent.getBooleanExtra("dust_check", false);
        fall_check = intent.getBooleanExtra("fall_check", false);
        Log.d("dust_check : ", String.valueOf(dust_check));
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
                    if(getDust_check() && getPermission_check() && isOnline() && isGetLocation()) {
                        new dust_XMLparser().execute();
                    }
                    if(getFall_check() && getPermission_check() && isOnline() && isGetLocation()) {
                        new weather_XMLparser().execute();
                    }

            }
            public void onLost(Network network) {
                Log.d("MyService", "테스트 onLost");

                openDatabase("databaseName");

                createTable();

                memoSelect();

                    if (getDust_check() && getPermission_check() && isGetLocation()) {
                        new dust_XMLparser().weatherNotificationService();
                    }
                    if (getFall_check() && getPermission_check() && isGetLocation()) {
                        new weather_XMLparser().fall_NotificationService();
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

    public class weather_XMLparser extends  AsyncTask<String, Void, Document>{
        Document doc;
        @Override
        protected Document doInBackground(String... urls){
            URL url;
            Calculation calculation = new Calculation();
            try{
                Time();
                Calculation.LatXLngY latXLngY = calculation.convertGRID_GPS(calculation.TO_GRID, lat,lon);
                StringBuilder urlBuilder = new StringBuilder("http://www.kma.go.kr/wid/queryDFS.jsp?"); /*URL*/
                urlBuilder.append(URLEncoder.encode("gridx","UTF-8") + "=" + URLEncoder.encode(String.valueOf((int)latXLngY.x),"UTF-8"));
                urlBuilder.append("&"+URLEncoder.encode("gridy","UTF-8") + "=" + URLEncoder.encode(String.valueOf((int)latXLngY.y),"UTF-8"));
                url = new URL(urlBuilder.toString());
                System.out.print(url);
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
            NodeList nodeList = doc.getElementsByTagName("data");

            Weather_time = nodeList.item(0).getChildNodes().item(1).getTextContent(); // 시간 "hour"
            temp = nodeList.item(0).getChildNodes().item(5).getTextContent(); // 현재기온 "temp"
            wfKor = nodeList.item(0).getChildNodes().item(15).getTextContent(); // 날씨상태 "wfKor"
            POP = nodeList.item(0).getChildNodes().item(19).getTextContent(); // 강수확률 "pop"

            super.onPostExecute(doc);
        }
        private void fall_NotificationService() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            int minTime = Integer.parseInt(Weather_time)-3;
            builder.setContentTitle("시간 : " + String.valueOf(minTime) + "~" + Weather_time);
            builder.setContentText("현재기온 : " + temp + " " + wfKor + " " + "강수확률 : " + POP );

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
                adminname();
                gooname();
                StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + dust_ServiceKey); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
                urlBuilder.append("&" + URLEncoder.encode("searchCondition","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간 (시간 : HOUR, 하루 : DAILY)*/
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
            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                if(sigoongo[2].equals(element.getElementsByTagName("cityName").item(0).getChildNodes().item(0).getNodeValue())){
                    NodeList datatime = element.getElementsByTagName("dataTime");
                    timeStr = datatime.item(0).getChildNodes().item(0).getNodeValue() + "\n";

                    NodeList area = element.getElementsByTagName("cityName");
                    areaStr = area.item(0).getChildNodes().item(0).getNodeValue();

                    NodeList dust_1 = element.getElementsByTagName("pm10Value");
                    dust = dust_1.item(0).getChildNodes().item(0).getNodeValue();

                    NodeList cho_dust_1 = element.getElementsByTagName("pm25Value");
                    cho_dust = cho_dust_1.item(0).getChildNodes().item(0).getNodeValue();
                }
            }

            super.onPostExecute(doc);
        }

        private void weatherNotificationService() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default");

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(areaStr + "미세먼지" + dust + " 초미세먼지" + cho_dust);
            if(Integer.parseInt(dust)<=30)
                builder.setContentText("미세먼지 좋음");
            if(30<Integer.parseInt(dust) && Integer.parseInt(dust)<=80)
                builder.setContentText("미세먼지 보통");
            if(Integer.parseInt(dust)>80 && Integer.parseInt(dust)<=150)
                builder.setContentText("미세먼지 나쁨");
            if(Integer.parseInt(dust)>150)
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

    public void adminname(){//지역
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try{
            address_List = geocoder.getFromLocation(lat,lon,10);
            address = address_List.get(0).getAdminArea();
            if(address.equals("경상남도"))
                address = "경남";
            if(address.equals("경상북도"))
                address = "경북";
            if(address.equals("충청북도"))
                address = "충북";
            if(address.equals("충청남도"))
                address = "충남";
            if(address.equals("경상북도"))
                address = "경북";
            if(address.equals("전라남도"))
                address = "전남";
            if(address.equals("전라북도"))
                address = "전북";
            if(address.equals("강원도"))
                address = "강원";
            if(address.equals("서울특별시"))
                address = "서울";
            if(address.equals("부산광역시"))
                address = "부산";
            if(address.equals("대구광역시"))
                address = "대구";
            if(address.equals("인천광역시"))
                address = "인천";
            if(address.equals("광주광역시"))
                address = "광주";
            if(address.equals("대전광역시"))
                address = "대전";
            if(address.equals("울산광역시"))
                address = "울산";
            if(address.equals("경기도"))
                address = "경기";
            if(address.equals("제주특별자치도"))
                address = "제주";
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void gooname(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            address_List = geocoder.getFromLocation(lat, lon,10);
            String address_name = address_List.get(0).getAddressLine(0).replace(",",""); // 전체주소
            sigoongo = address_name.split(" "); // 지역으로나누기위해 공백기준으로 문자열 자름
            Log.d("goo_name : ", sigoongo[2]); // 지역
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Time(){
        long now = System.currentTimeMillis();
        Date date;
        date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat T_simpleDateFormat = new SimpleDateFormat("HH",Locale.KOREA);
        Year_Month_Day = simpleDateFormat.format(now);
        Timer = T_simpleDateFormat.format(now);
    }

    private static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";
    private static class Check extends Thread{
        private boolean success;
        private String host;

        public Check(String host){
            this.host = host;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try{
                conn = (HttpURLConnection) new URL(host).openConnection();
                conn.setRequestProperty("User-Agent","Android");
                conn.setConnectTimeout(1000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == 204){
                    success = true;
                }else{
                    success = false;
                }
            }catch(Exception e){
                success = false;
                e.printStackTrace();
            }
            if(conn != null){
                conn.disconnect();
            }
            super.run();
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public boolean isOnline(){
        Check cc = new Check(CONNECTION_CONFIRM_CLIENT_URL);
        cc.start();
        try{
            cc.join();
            return cc.isSuccess();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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

    public void setPermission_check(Boolean isPermission){
        this.permission_check = permission_check;
    }

    public boolean getPermission_check(){
        return permission_check;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
  //      throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
}

