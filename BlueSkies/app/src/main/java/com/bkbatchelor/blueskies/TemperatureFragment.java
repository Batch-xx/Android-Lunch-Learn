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
import android.widget.Button;
import android.widget.TextView;

import static com.bkbatchelor.blueskies.WeatherService.PRESENT_TEMP_EVENT;
import static com.bkbatchelor.blueskies.WeatherService.TEMPERATURE_KEY;
import static com.bkbatchelor.blueskies.WeatherService.TEMPERATURE_SEND_EVENT;


public class TemperatureFragment extends Fragment {
    private Context context;
    private double presentTemp;
    private double lowTemp;
    private double hiTemp;
    private boolean isFahrenheit = false;

    private String PRESENT_TEMP = "present_temp";
    private String LOW_TEMP = "low_temp";
    private String HI_TEMP = "hi_temp";
    private String IS_FAHRENHEIT = "is_fahrenheit";

    private IntentFilter temperatureSendEventFilter = new IntentFilter(TEMPERATURE_SEND_EVENT);

    private BroadcastReceiver temperatureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WeatherParcelable weatherParcelable = intent.getParcelableExtra(TEMPERATURE_KEY);

            presentTemp = weatherParcelable.getPresentTemp();
            lowTemp = weatherParcelable.getLowTemp();
            hiTemp = weatherParcelable.getHighTemp();
            setTemperatures(presentTemp, lowTemp, hiTemp);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            this.isFahrenheit = savedInstanceState.getBoolean(IS_FAHRENHEIT);
            this.presentTemp = savedInstanceState.getDouble(PRESENT_TEMP);
            this.lowTemp = savedInstanceState.getDouble(LOW_TEMP);
            this.hiTemp = savedInstanceState.getDouble(HI_TEMP);
        }
        return inflater.inflate(R.layout.temperature_frag, parent, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setTemperatures(presentTemp, lowTemp, hiTemp);
        context.registerReceiver(temperatureReceiver, temperatureSendEventFilter);
    }


    @Override
    public void onStop() {
        super.onStop();

        context.unregisterReceiver(temperatureReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(PRESENT_TEMP,presentTemp);
        outState.putDouble(LOW_TEMP, lowTemp);
        outState.putDouble(HI_TEMP, hiTemp);
        outState.putBoolean(IS_FAHRENHEIT, isFahrenheit);
        super.onSaveInstanceState(outState);
    }

    public void onClickTemperatureSwitch(View view) {
        Button buttonText = (Button)view;
        if(isFahrenheit){
            isFahrenheit = false;
            buttonText.setText(getText(R.string.celsius));
            setTemperatures(presentTemp, lowTemp, hiTemp);
        }else{
            isFahrenheit = true;
            buttonText.setText(getText(R.string.fahrenheit));
            setTemperatures(presentTemp, lowTemp, hiTemp);
        }
    }

    private void sendEvent(String temp){
        Intent sendEventIntent = new Intent();
        sendEventIntent.setAction(PRESENT_TEMP_EVENT);
        sendEventIntent.putExtra("present_temp",temp);
        getActivity().sendBroadcast(sendEventIntent);
    }

    private void setTemperatures(double presentTemp, double lowTemp, double hiTemp) {
        double convertPresentTemp;
        double convertlowTemp;
        double convertHiTemp;

        TextView presentTempView = (TextView) getActivity()
                .findViewById(R.id.present_temp_textview_temperature_frag);
        TextView lowTempView = (TextView) getActivity()
                .findViewById(R.id.low_temp_textview_temperature_frag);
        TextView hiTempView = (TextView) getActivity()
                .findViewById(R.id.hi_temp_textview_temperature_frag);
        Button tempSwitch = (Button) getActivity()
                .findViewById(R.id.temp_switch_button_temperature_frag);


        if(isFahrenheit){
            convertPresentTemp = convertKelvinToFahrenheit(presentTemp);
            convertlowTemp = convertKelvinToFahrenheit(lowTemp);
            convertHiTemp = convertKelvinToFahrenheit(hiTemp);
            tempSwitch.setText(getText(R.string.fahrenheit));

        }else{
            convertPresentTemp = convertKelvinToCelius(presentTemp);
            convertlowTemp = convertKelvinToCelius(lowTemp);
            convertHiTemp = convertKelvinToCelius(hiTemp);
            tempSwitch.setText(getText(R.string.celsius));
        }

        presentTempView.setText(String.valueOf((int)convertPresentTemp) + "\u00B0");
        lowTempView.setText(String.valueOf((int)convertlowTemp) + "\u00B0");
        hiTempView.setText(String.valueOf((int)convertHiTemp) + "\u00B0");
        sendEvent(String.valueOf((int)convertPresentTemp) + "\u00B0");
    }

    private double convertKelvinToFahrenheit(double degreeKelvin) {
        return degreeKelvin * 1.8 - 459.67;
    }

    private double convertKelvinToCelius(double degreeKelvin){
        return degreeKelvin - 273.15;
    }

}
