package com.example.aaron.stormy.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.model.HourlyWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Next12Hours extends AppCompatActivity {

    private final static String TAG = Next12Hours.class.getSimpleName();

    private TextView[] mTimes = new TextView[12];
    private ImageView[] mIcons = new ImageView[12];
    private TextView[] mTemperatures = new TextView[12];
    private TextView[] mPrecipChances = new TextView[12];

    private ConstraintLayout mHourlyWeatherLayout;
    private TextView returnTextView;
    private String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next12_hours);

        mHourlyWeatherLayout = (ConstraintLayout) findViewById(R.id.hourly_weather_layout);
        returnTextView = (TextView) findViewById(R.id.returnTextView);

        Intent intent = getIntent();
        int color = intent.getIntExtra(getString(R.string.bkg_color), 8);
        mHourlyWeatherLayout.setBackgroundColor(color);
        jsonData = intent.getStringExtra(getString(R.string.json_data));

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getHourlyViews();
        getHourlyDetails();

    }

    private void getHourlyDetails() {
        for(int i = 0; i < 12; i++){
            try {
                HourlyWeather hourlyWeather = getHourlyWeather(jsonData, i + 1);
                mTimes[i].setText(hourlyWeather.getFormattedTime() + "");
                mTemperatures[i].setText(hourlyWeather.getTemperature() + "\u2109");
                Drawable drawable = ContextCompat.getDrawable(Next12Hours.this, hourlyWeather.getIconId());
                mIcons[i].setImageDrawable(drawable);
                mPrecipChances[i].setText("            " + hourlyWeather.getPrecipChance() + "%");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Finished populating the various View arrays.");
    }

    private void getHourlyViews() {
        String hourLabel;
        String iconLabel;
        String tempLabel;
        String precipLabel;
        int resID = 0;
        for(int i = 0; i < mTimes.length; i++){
            hourLabel = "hourLabel_" + i;
            resID = getResources().getIdentifier(hourLabel, "id", getPackageName());
            mTimes[i] = (TextView) findViewById(resID);
            iconLabel = "iconImageLabel_" + i;
            resID = getResources().getIdentifier(iconLabel, "id", getPackageName());
            mIcons[i] = (ImageView) findViewById(resID);
            tempLabel = "temperatureLabel_" + i;
            resID = getResources().getIdentifier(tempLabel, "id", getPackageName());
            mTemperatures[i] = (TextView) findViewById(resID);
            precipLabel = "precipLabel_" + i;
            resID = getResources().getIdentifier(precipLabel, "id", getPackageName());
            mPrecipChances[i] = (TextView) findViewById(resID);

        }
    }

    private HourlyWeather getHourlyWeather(String jsonData, int index) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");

        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONObject hour = hourly.getJSONArray("data").getJSONObject(index);

        HourlyWeather hourlyWeather = new HourlyWeather();
        hourlyWeather.setTimeZone(timeZone);
        hourlyWeather.setIcon(hour.getString("icon"));
        hourlyWeather.setTime(hour.getLong("time"));
        hourlyWeather.setTemperature(hour.getDouble("temperature"));
        hourlyWeather.setPrecipChance(hour.getDouble("precipProbability"));

        return hourlyWeather;
    }
}
