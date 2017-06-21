package com.bkbatchelor.blueskies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class TemperatureFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.temperature_frag, parent, false);
    }

    public void onClickTemperatureSwitch(View view){
        Toast.makeText(getActivity(), "clicked on switch", Toast.LENGTH_SHORT).show();
    }
}
