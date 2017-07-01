package com.example.aaron.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.adapters.HourAdapter;
import com.example.aaron.stormy.weather.Hour;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyForecast extends AppCompatActivity {

    private final static String TAG = HourlyForecast.class.getSimpleName();

    private Hour[] mHours;

    @BindView(R.id.returnButton) ImageView mReturnButton;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.hourly_weather_layout) ConstraintLayout mHourlyForecastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Drawable background = ContextCompat.getDrawable(this, MainActivity.BACKGROUND_ID);
        mHourlyForecastLayout.setBackground(background);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(this, mHours);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }
}
