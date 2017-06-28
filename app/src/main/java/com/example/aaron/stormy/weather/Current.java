package com.example.aaron.stormy.weather;

import android.util.Log;

import com.example.aaron.stormy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Current {
    private String mLocation;
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;
    private double mApparentTemperature;
    private double mWindSpeed;
    private int mUVIndex;
    private int mColorId;
    private double mWindDirection;

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public int getIconId(){
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId = R.drawable.clear_day;

        if(mIcon.equals("clear-day")){
            iconId = R.drawable.clear_day;
            mColorId = 0;
        }
        else if(mIcon.equals("clear-night")){
            iconId = R.drawable.clear_night;
            mColorId = 1;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.drawable.rain;
            mColorId = 2;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.drawable.snow;
            mColorId = 3;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.drawable.sleet;
            mColorId = 4;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.drawable.wind;
            mColorId = 5;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.drawable.fog;
            mColorId = 6;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
            mColorId = 7;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
            mColorId = 8;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
            mColorId = 9;
        }
        else{
            iconId = R.drawable.partly_cloudy;
            mColorId = 8;
        }

        return iconId;
    }

    public long getmTime() {
        return mTime;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getmTime() * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public int getmTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setmTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public int getmHumidity() {
        double humidityPercentage = mHumidity * 100;
        return (int)Math.round(humidityPercentage);
    }

    public void setmHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getmPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setmPrecipChance(double mPrecipChance) {
        this.mPrecipChance = mPrecipChance;
    }

    public String getmSummary() {
        return mSummary;
    }

    public void setmSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public int getApparentTemperature() {
        return (int) Math.round(mApparentTemperature);
    }

    public void setApparentTemperature(double apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public int getWindSpeed() {
        return (int) Math.round(mWindSpeed);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public int getUVIndex() {
        return mUVIndex;
    }

    public void setUVIndex(int UVIndex) {
        mUVIndex = UVIndex;
    }

    public int getColorId() {
        return mColorId;
    }

    public String getWindDirection() {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round((mWindDirection % 360) / 45);
        Log.i("WIND DIRECTION INDEX", "index is: " + index);
        return dirs[index - 1];
    }

    public void setWindDirection(double windDirection) {
        mWindDirection = windDirection;
    }
}
