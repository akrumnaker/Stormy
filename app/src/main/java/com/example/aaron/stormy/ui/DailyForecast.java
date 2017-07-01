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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.adapters.DayAdapter;
import com.example.aaron.stormy.weather.Day;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecast extends ListActivity {

    public final static String DAY_FORECAST = "DAY_FORECAST";
    private final static String TAG = DailyForecast.class.getSimpleName();

    @BindView(R.id.returnButton) ImageView mReturnButton;
    @BindView(R.id.daily_weather_layout) ConstraintLayout mDailyForecastLayout;
    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        Drawable background = ContextCompat.getDrawable(this, MainActivity.BACKGROUND_ID);
        mDailyForecastLayout.setBackground(background);
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Can create Toast message or even start new activity here
        /*
        String dayOfTheWeek = mDays[position].getDayOfWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getHighTemperature() + "\u2109";
        String message = String.format("On %s, the high will be %s, and it will be %s",
                dayOfTheWeek, highTemp, conditions);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        */
        Intent intent = new Intent(DailyForecast.this, DayForecast.class);
        intent.putExtra(DAY_FORECAST, mDays[position]);
        startActivity(intent);
    }
}
