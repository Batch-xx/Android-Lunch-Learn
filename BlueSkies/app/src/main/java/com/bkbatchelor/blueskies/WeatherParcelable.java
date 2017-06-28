package com.bkbatchelor.blueskies;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherParcelable implements Parcelable{
    private double presentTemp;
    private double lowTemp;
    private double highTemp;

    public static Parcelable.Creator<WeatherParcelable> CREATOR
            = new Parcelable.Creator<WeatherParcelable>(){

        @Override
        public WeatherParcelable createFromParcel(Parcel parcel) {
            return new WeatherParcelable(parcel);
        }

        @Override
        public WeatherParcelable[] newArray(int size) {
            return new WeatherParcelable[size];
        }
    };

    public WeatherParcelable(double presentTemp, double lowTemp, double highTemp){
        this.presentTemp = presentTemp;
        this.lowTemp = lowTemp;
        this.highTemp = highTemp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    private WeatherParcelable(Parcel in){
        presentTemp = in.readDouble();
        lowTemp = in.readDouble();
        highTemp = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(presentTemp);
        out.writeDouble(lowTemp);
        out.writeDouble(highTemp);
    }

    public double getPresentTemp() {
        return presentTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public double getHighTemp() {
        return highTemp;
    }
}
