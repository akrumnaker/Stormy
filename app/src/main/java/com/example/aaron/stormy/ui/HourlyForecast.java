package com.example.aaron.stormy.ui;

import android.app.ListActivity;
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
import com.example.aaron.stormy.weather.Hour;

import org.json.JSONException;
import org.json.JSONObject;

public class HourlyForecast extends ListActivity {

    private final static String TAG = HourlyForecast.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

    }
}
