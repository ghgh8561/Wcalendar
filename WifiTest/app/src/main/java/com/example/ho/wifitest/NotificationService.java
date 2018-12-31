package com.example.ho.wifitest;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service implements Runnable{
    String connect_notwork_check;
    private static final int REBOOT_DELAY_TIMER = 5 * 1000; //??
    private static final int LOCATION_UPDATE_DELAY = 5 * 1000; // 실행 주기
    private Handler mHandler;
    private boolean mIsRunning;
    private int mStartId = 0;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm();

        super.onCreate();

        mIsRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartId = startId;

        mHandler = new Handler();
        mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
        mIsRunning = true;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        registerRestartAlarm();
        super.onDestroy();
        mIsRunning = false;
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void run() {
        /*if(!mIsRunning)
        {
            Toast.makeText(this, "!mIsRunning", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
            mIsRunning = true;

        } else {
            function();
            Toast.makeText(this, connect_notwork_check, Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
            mIsRunning = true;
        }*/
        function();
        Toast.makeText(this, connect_notwork_check, Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
        mIsRunning = true;

    }

    private void function() {
        WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        String mac = info.getBSSID();

        netWork();

        if(connect_notwork_check == "WIFI") {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

            //String b = String.valueOf(a);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("알림 제목");
            builder.setContentText("알림 내용");

            Intent intent = new Intent(this, NotificationService.class);
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

    }

    private boolean netWork(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();
        if(ninfo == null){
            return false;
        }else{
            if (ninfo.getType() == ConnectivityManager.TYPE_WIFI){
                connect_notwork_check = "WIFI";
            } else if (ninfo.getType() == ConnectivityManager.TYPE_MOBILE){
                connect_notwork_check = "MOBILE";
            }
            return true;
        }
    }

    private void registerRestartAlarm() {

        Intent intent = new Intent(NotificationService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(NotificationService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += REBOOT_DELAY_TIMER; // 5초 후에 알람이벤트 발생

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,REBOOT_DELAY_TIMER, sender);
    }

    private void unregisterRestartAlarm() {

        Intent intent = new Intent(NotificationService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(NotificationService.this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
}
