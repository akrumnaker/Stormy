package com.example.aaron.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.aaron.stormy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Day implements Parcelable{

    private String mIcon;
    private long mTime;
    private double mLowTemperature;
    private double mHighTemperature;
    private double mPrecipChance;
    private String mTimeZone;
    private String mSummary;
    private double mWindDirection;
    private double mWindSpeed;
    private int mUVIndex;
    private double mHumidity;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public String getDayOfWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE\nd MMMM");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getLowTemperature() {
        return (int) Math.round(mLowTemperature);
    }

    public void setLowTemperature(double lowTemperature) {
        mLowTemperature = lowTemperature;
    }

    public int getHighTemperature() {
        return (int) Math.round(mHighTemperature);
    }

    public void setHighTemperature(double highTemperature) {
        mHighTemperature = highTemperature;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getWindDirection() {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round((mWindDirection % 360) / 45);
        Log.i("WIND DIRECTION INDEX", "index is: " + index);
        return dirs[index - 1];    }

    public void setWindDirection(double windDirection) {
        mWindDirection = windDirection;
    }

    public double getWindSpeed() {
        return mWindSpeed;
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

    public int getHumidity() {
        double humidityPercentage = mHumidity * 100;
        return (int)Math.round(humidityPercentage);
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mHighTemperature);
        dest.writeDouble(mLowTemperature);
        dest.writeString(mIcon);
        dest.writeString(mTimeZone);
        dest.writeDouble(mPrecipChance);
        dest.writeDouble(mHumidity);
        dest.writeInt(mUVIndex);
        dest.writeDouble(mWindDirection);
        dest.writeDouble(mWindSpeed);
    }

    private Day(Parcel in){
        mTime = in.readLong();
        mSummary = in.readString();
        mHighTemperature = in.readDouble();
        mLowTemperature = in.readDouble();
        mIcon = in.readString();
        mTimeZone = in.readString();
        mPrecipChance = in.readDouble();
        mHumidity = in.readDouble();
        mUVIndex = in.readInt();
        mWindDirection = in.readDouble();
        mWindSpeed = in.readDouble();
    }

    public Day(){}

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
