package com.bkbatchelor.blueskies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static com.bkbatchelor.blueskies.WeatherService.TEMPERATURE_SEND_EVENT;


public class TemperatureFragment extends Fragment {
    private Context context;

    private IntentFilter temperatureEventFilter = new IntentFilter(TEMPERATURE_SEND_EVENT);
    private BroadcastReceiver temperatureReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            Toast.makeText(context, "Broadcast Received", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        context.registerReceiver(temperatureReceiver, temperatureEventFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.temperature_frag, parent, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        context.unregisterReceiver(temperatureReceiver);
    }

    public void onClickTemperatureSwitch(View view){
        Toast.makeText(context, "clicked on switch", Toast.LENGTH_SHORT).show();
    }
}
