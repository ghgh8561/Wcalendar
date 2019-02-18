package com.wcalendar.klp.wcalendar;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class MyService extends Service implements LocationListener{
    private static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";//인터넷연결상태를 확인하기위한 url
    dust_parser dustParser;// 미세먼지 파싱 클래스
    POP_parser popParser; // 기상청날씨파싱

    boolean isGPSEnable = false;//gps사용가능여부
    boolean isNetWorkEnable = false;//네트워크사용가능여부
    boolean isGetLocation = false;//location사용여부
    Location location;
    double lat;
    double lon;

    public Location getLocation(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission
                (getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission
                (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try{
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnable && !isNetWorkEnable){
                //gps / 네트워크 둘다사용불가일때
            }else{
                isGetLocation = true;
                if(isNetWorkEnable){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10,1, this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }if(location != null){
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }if(isGPSEnable){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,1, this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }if(location != null){
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public void MyLocation(){
        getLocation();
    }

    public MyService() {
    }

    @Override
    public void onCreate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(1, new Notification());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLocation();
        Network();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isOnline() { // 인터넷연결감지 메소드(연결은되어있지만 인터넷사용불가를 테스트하기위함)
        Internet_state_check internet_state_check = new Internet_state_check(CONNECTION_CONFIRM_CLIENT_URL);
        internet_state_check.start();
        try {
            internet_state_check.join();
            return internet_state_check.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void notifycation() { //알람서비스 샘플 수정필요함
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); // 기기 소리설정을 가져오기위함

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Wcalender")
                .setContentText("실행중")
                .setAutoCancel(true);

        //기기소리설정에 따른 notify소리설정
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(0, mBuilder.build());
    }

    private void Network() { //와이파이 연결끊어짐과 연결됨 상태체크
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI); // 와이파이지정

        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {//Wifi 연결됨
                if (isOnline()) {//Wifi 연결이 되어있고, 인터넷이 사용가능할때
                    if(isGetLocation == true) {// 위치정보가 켜져있을때
                        if(isRainSwitchset()) {
                            popParser = new POP_parser(lat,lon);
                            popParser.execute();
                        }
                        if(isDustSwitchset()) {
                            dustParser = new dust_parser(getApplicationContext(), lat, lon);
                            dustParser.execute();
                        }
                    }if(isGetLocation == false){ // 위치정보가꺼져있을때 위치정보켜라는 팝업창
                        Intent popup = new Intent(getApplicationContext(),NoLocationDialog.class);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0, popup,PendingIntent.FLAG_ONE_SHOT);
                        try{
                            pi.send();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onLost(Network network) {//Wifi 끊어짐
                if(isGetLocation) {
                    if(isDustSwitchset()) {
                        dust_notifycation();
                    }
                    if(isRainSwitchset() && Integer.parseInt(popParser.POP)>= isRainNumset()) {
                        POP_notifycation();
                    }
                }
                notifycation();
            }
        });
    }

    public boolean isRainSwitchset(){
        SharedPreferences sharedPreferences = getSharedPreferences("rain and dust",0);
        boolean RainSwitchState = sharedPreferences.getBoolean("rain_check",false);
        return RainSwitchState;
    }

    public int isRainNumset(){
        SharedPreferences sharedPreferences = getSharedPreferences("rain and dust", 0);
        String Rain_num = sharedPreferences.getString("rain_num",null);
        int Rain_pop;
        if(Rain_num == null)
            Rain_pop = 0;
        else
            Rain_pop = Integer.parseInt(Rain_num);
        return Rain_pop;
    }

    public boolean isDustSwitchset(){
        SharedPreferences sharedPreferences = getSharedPreferences("rain and dust", 0);
        boolean DustSwitchState = sharedPreferences.getBoolean("dust_check", false);
        return DustSwitchState;
    }

    public void dust_notifycation() { //미세먼지
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); // 기기 소리설정을 가져오기위함

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.btn_star)
                .setAutoCancel(true);

        //기기소리설정에 따른 notify소리설정
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        String dust_msg = "";
        String cho_dust_msg = "";
        //미세먼지 농도에따른 좋음,보통,나쁨,매우나쁨
        while(true){
            try{
                if (Integer.parseInt(dustParser.PM10_) <= 30) dust_msg = "좋음";
                if (Integer.parseInt(dustParser.PM10_) > 30 && Integer.parseInt(dustParser.PM10_) <= 80)
                    dust_msg = "보통";
                if (Integer.parseInt(dustParser.PM10_) > 80 && Integer.parseInt(dustParser.PM10_) <= 150)
                    dust_msg = "나쁨";
                if (Integer.parseInt(dustParser.PM10_) > 150) dust_msg = "매우나쁨";
                break;
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }

        //초미세먼지 농도에따른 좋음,보통,나쁨,매우나쁨
        while(true) {
            try {
                if (Integer.parseInt(dustParser.PM25_) <= 15) cho_dust_msg = "좋음";
                if (Integer.parseInt(dustParser.PM25_) > 15 && Integer.parseInt(dustParser.PM25_) <= 35)
                    cho_dust_msg = "보통";
                if (Integer.parseInt(dustParser.PM25_) > 35 && Integer.parseInt(dustParser.PM25_) <= 75)
                    cho_dust_msg = "나쁨";
                if (Integer.parseInt(dustParser.PM25_) > 75) cho_dust_msg = "매우나쁨";
                break;
            } catch (NullPointerException e) {

                e.printStackTrace();
            }
        }

        City city = new City(getApplicationContext(), lat, lon);
        mBuilder.setContentTitle(city.City_name());
        mBuilder.setContentText("미세먼지 : " + dustParser.PM10_ + "(" + dust_msg + ")" + " " +
                "초미세먼지 : " + dustParser.PM25_ + "(" + cho_dust_msg + ")");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, mBuilder.build());
    }

    public void POP_notifycation() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); // 기기 소리설정을 가져오기위함

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(android.R.drawable.btn_star)
                .setAutoCancel(true);

        //기기소리설정에 따른 notify소리설정
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        else mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        City city = new City(getApplicationContext(), lat, lon);
        mBuilder.setContentTitle(city.full_name());
        mBuilder.setContentText("기온 : " + popParser.Temperatures + " " + "날씨 : " + popParser.wkKor + " " + "강수확률 : " + popParser.POP);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(2, mBuilder.build());
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
