package com.bkbatchelor.blueskies;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class WeatherService extends Service {
    private Thread weatherThread = null;
    private int NOTIFICATION_ID = 1333;
    private int sleepIntervalMillisec = 60000;
    private boolean ThreadIsRunning = true;
    private String  todayTemperature = "0\u00B0";
    private String TAG = WeatherService.class.getSimpleName();

    public static String TEMPERATURE_SEND_EVENT = "com.bkbatchelor.blueskies.TEMPERATURE_SEND_EVENT";
    public static String TEMPERATURE_KEY = "com.bkbatchelor.blueskies.TEMPERATURE_KEY";


    @Override
    public void onCreate() {
        super.onCreate();
        weatherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(ThreadIsRunning) {
                    sendEvent("45");

                    try{
                        Thread.sleep(sleepIntervalMillisec);
                    }catch(Exception e){
                        Log.e(TAG, e.toString());
                    }
                }
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

    public void setThreadIsRunning(boolean threadIsRunning) {
        ThreadIsRunning = threadIsRunning;
    }

    private void sendEvent(String temperature){
        Intent sendEventIntent = new Intent();
        sendEventIntent.setAction(TEMPERATURE_SEND_EVENT);
        sendEventIntent.putExtra(TEMPERATURE_KEY,temperature);
        sendBroadcast(sendEventIntent);
    }
}
