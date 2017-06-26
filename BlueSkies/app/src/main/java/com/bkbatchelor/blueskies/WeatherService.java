package com.bkbatchelor.blueskies;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;


public class WeatherService extends Service {
    private Thread weatherThread = null;
    private int NOTIFICATION_ID = 1333;
    private String  todayTemperature = "0\u00B0";

    @Override
    public void onCreate() {
        super.onCreate();
        weatherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Communicate with OpenWeather API
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        startForeground(NOTIFICATION_ID,getNotification());

        if(!weatherThread.isAlive()) {
            weatherThread.start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(todayTemperature)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent);

        return builder.build();
    }
}
