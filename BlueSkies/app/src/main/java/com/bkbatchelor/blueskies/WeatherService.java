package com.bkbatchelor.blueskies;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Parcel;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bkbatchelor.blueskies.model.WeatherModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherService extends Service {
    private Thread weatherThread = null;
    private int NOTIFICATION_ID = 1333;
    private int sleepIntervalMillisec = 3600000;
    private boolean ThreadIsRunning = true;
    private String  todayTemperature = "0\u00B0";
    private OkHttpClient client = new OkHttpClient();
    private String TAG = WeatherService.class.getSimpleName();

    public static String TEMPERATURE_SEND_EVENT = "com.bkbatchelor.blueskies.ServiceTEMPERATURE_SEND_EVENT";
    public static String TEMPERATURE_KEY = "com.bkbatchelor.blueskies.ServiceTEMPERATURE_KEY";
    public static String PRESENT_TEMP_EVENT = "com.bkbatchelor.blueskies.Service.PRESENT_TEMP_EVENT";

    private IntentFilter presentTempEventFilter = new IntentFilter(PRESENT_TEMP_EVENT);

    private BroadcastReceiver presentTempReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            String temp = intent.getStringExtra("present_temp");
            if(temp != null){
                todayTemperature = temp + "\u00B0";
            }else{
                todayTemperature = "--\u00B0";
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification =
                    new Notification.Builder(WeatherService.this)
                            .setContentTitle(getText(R.string.notification_title))
                            .setContentText(todayTemperature)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentIntent(getPendingIntent())
                            .build();

            notificationManager.notify(NOTIFICATION_ID,notification);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        weatherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(ThreadIsRunning) {
                    String  body = null;
                    Request request = new Request.Builder()
                            .url("http://api.openweathermap.org/data/2.5/weather?zip=07728,us&appid=04fcf857a9e769c45f9ab6141c8ccb0e")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        if(!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        body  = response.body().string();
                    }catch(IOException e){
                        Log.e(TAG, e.toString());
                    }

                    WeatherModel weatherModel = jsonToWeatherModel(body);
                    WeatherParcelable weatherParcelable =  new WeatherParcelable(
                            weatherModel.getMain().getTemp(),
                            weatherModel.getMain().getTempMin(),
                            weatherModel.getMain().getTempMax());

                    sendEvent(weatherParcelable);

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

        registerReceiver(presentTempReceiver, presentTempEventFilter);

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
        unregisterReceiver(presentTempReceiver);

        if(weatherThread.isAlive()) {
            weatherThread.interrupt();
        }
    }

    private PendingIntent getPendingIntent(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        return  PendingIntent.getActivity(this, 0, notificationIntent, 0);
    }

    private Notification getNotification() {
        Notification notification=
                new Notification.Builder(this)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(todayTemperature)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(getPendingIntent())
                        .build();

        return notification;
    }

    public void setThreadIsRunning(boolean threadIsRunning) {
        ThreadIsRunning = threadIsRunning;
    }

    private void sendEvent(WeatherParcelable parcelable){
        Intent sendEventIntent = new Intent();
        sendEventIntent.setAction(TEMPERATURE_SEND_EVENT);
        sendEventIntent.putExtra(TEMPERATURE_KEY,parcelable);
        sendBroadcast(sendEventIntent);
    }

    private WeatherModel jsonToWeatherModel(String response) {
        Gson gson = new GsonBuilder().create();
        WeatherModel weatherModel = gson.fromJson(response,WeatherModel.class);
        return  weatherModel;
    }

}
