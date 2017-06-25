package com.bkbatchelor.blueskies;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class WeatherService extends Service {
    private Thread weatherThread = null;

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

        if(!weatherThread.isAlive()) {
            weatherThread.start();

            // For Demonstration only
            if(weatherThread.isAlive()) {
                Toast.makeText(this, "Weather Service is running", Toast.LENGTH_SHORT).show();
            }
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
}
