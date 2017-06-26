package com.example.aaron.stormy.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.model.DailyWeather;
import com.example.aaron.stormy.model.HourlyWeather;

import org.json.JSONException;
import org.json.JSONObject;

public class Next7Days extends AppCompatActivity {

    private final static String TAG = Next7Days.class.getSimpleName();

    private TextView[] mTimes = new TextView[12];
    private ImageView[] mIcons = new ImageView[12];
    private TextView[] mTemperatures = new TextView[12];
    private TextView[] mPrecipChances = new TextView[12];

    private ConstraintLayout mDailyWeatherLayout;
    private TextView returnTextView;
    private String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next7_days);
        mDailyWeatherLayout = (ConstraintLayout) findViewById(R.id.daily_weather_layout);
        returnTextView = (TextView) findViewById(R.id.returnTextView);

        Intent intent = getIntent();
        int color = intent.getIntExtra(getString(R.string.bkg_color), 8);
        mDailyWeatherLayout.setBackgroundColor(color);
        jsonData = intent.getStringExtra(getString(R.string.json_data));

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDailyViews();
        getDailyDetails();
    }

    private void getDailyDetails() {
        for(int i = 0; i < 12; i++){
            try {
                DailyWeather dailyWeather = getDailyWeather(jsonData, i + 1);
                mTimes[i].setText(dailyWeather.getFormattedTime() + "");
                mTemperatures[i].setText(dailyWeather.getHighTemperature() + "\u2109\n" + dailyWeather.getLowTemperature() + "\u2109");
                Drawable drawable = ContextCompat.getDrawable(Next7Days.this, dailyWeather.getIconId());
                mIcons[i].setImageDrawable(drawable);
                mPrecipChances[i].setText("            " + dailyWeather.getPrecipChance() + "%");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Finished populating the various View arrays.");
    }

    private void getDailyViews() {
        String hourLabel;
        String iconLabel;
        String tempLabel;
        String precipLabel;
        int resID = 0;
        for(int i = 0; i < mTimes.length; i++){
            hourLabel = "day_" + i;
            resID = getResources().getIdentifier(hourLabel, "id", getPackageName());
            mTimes[i] = (TextView) findViewById(resID);
            iconLabel = "day_icon_" + i;
            resID = getResources().getIdentifier(iconLabel, "id", getPackageName());
            mIcons[i] = (ImageView) findViewById(resID);
            tempLabel = "day_temperatures_" + i;
            resID = getResources().getIdentifier(tempLabel, "id", getPackageName());
            mTemperatures[i] = (TextView) findViewById(resID);
            precipLabel = "day_precip_" + i;
            resID = getResources().getIdentifier(precipLabel, "id", getPackageName());
            mPrecipChances[i] = (TextView) findViewById(resID);

        }
    }

    private DailyWeather getDailyWeather(String jsonData, int index) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONObject day = daily.getJSONArray("data").getJSONObject(index);

        DailyWeather dailyWeather = new DailyWeather();
        dailyWeather.setTimeZone(timeZone);
        dailyWeather.setIcon(day.getString("icon"));
        dailyWeather.setTime(day.getLong("time"));
        dailyWeather.setHighTemperature(day.getDouble("temperatureMax"));
        dailyWeather.setLowTemperature(day.getDouble("temperatureMin"));
        dailyWeather.setPrecipChance(day.getDouble("precipProbability"));

        return dailyWeather;
    }

}
