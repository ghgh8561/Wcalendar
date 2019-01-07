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
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    ConnectivityManager cm;
    SQLiteDatabase database;
    String mac_address;
    String title;
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
            }
            public void onLost(Network network) {
                Log.d("MyService", "테스트 onLost");

                openDatabase("databaseName");

                createTable();

                selectData();
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
            String sql = "CREATE TABLE IF NOT EXISTS " + "tableName" + "(_id integer PRIMARY KEY autoincrement, mac text)";
            database.execSQL(sql);

            String sql2 = "CREATE TABLE IF NOT EXISTS " + "memo" + "(mac text, title text)";
            database.execSQL(sql2);

            Log.d("MyService", "테이블 생성됨.");
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void selectData() {
        Log.d("MyService", "selectData() 호출됨.");

        WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        mac_address = info.getBSSID();

        if (database != null) {
            String sql = "Select mac FROM " + "tableName" + " WHERE mac = '" + mac_address + "'";
            Cursor cursor = database.rawQuery(sql, null);

            if (cursor.getCount() != 0) {
                notificationService();
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    private void notificationService() {
        memoSelect();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(mac_address);
        builder.setContentText(title);

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
        Log.d("MyService", "selectData() 호출됨.");

        if (database != null) {
            String sql = "SELECT mac, title FROM " + "memo" + " WHERE mac = '" + mac_address + "'";
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String mac = cursor.getString(0);
                title = cursor.getString(1);

                Log.d("MyService", "#" + i + "->" + mac + "->" + title);
            }

            cursor.close();
        } else {
            Log.d("MyService", "먼저 데이터베이스를 오픈하세요.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
