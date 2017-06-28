package com.example.aaron.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.adapters.DayAdapter;
import com.example.aaron.stormy.weather.Day;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class DailyForecast extends ListActivity {

    private final static String TAG = DailyForecast.class.getSimpleName();

    private ConstraintLayout mDailyForecastLayout;
    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        mDailyForecastLayout = (ConstraintLayout) findViewById(R.id.daily_weather_layout);
        Intent intent = getIntent();
        int backgroundId = intent.getIntExtra(getString(R.string.bkg_color), 8);
        Drawable background = ContextCompat.getDrawable(this, backgroundId);
        mDailyForecastLayout.setBackground(background);
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
    }

}
