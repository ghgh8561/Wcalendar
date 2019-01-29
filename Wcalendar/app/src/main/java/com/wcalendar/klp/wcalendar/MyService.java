package com.wcalendar.klp.wcalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    private static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";//인터넷연결상태를 확인하기위한 url

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifycation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isOnline(){ // 인터넷연결감지 메소드(연결은되어있지만 인터넷사용불가를 테스트하기위함)
        Internet_state_check internet_state_check = new Internet_state_check(CONNECTION_CONFIRM_CLIENT_URL);
        internet_state_check.start();
        try{
            internet_state_check.join();
            return internet_state_check.isSuccess();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void notifycation(){ //알람서비스 샘플 수정필요함
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this,"default")
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Wcalender")
                .setContentText("실행중")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(0,mBuilder.build());
    }
}
