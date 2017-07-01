package com.example.aaron.stormy.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.weather.Day;
import com.example.aaron.stormy.weather.DayItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayForecast extends AppCompatActivity {

    private static final String TAG = DayForecast.class.getSimpleName();

    private Day mDay;

    @BindView(R.id.returnButton) ImageView mReturnButton;
    @BindView(R.id.dayForecast) ConstraintLayout mDayForecastLayout;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.temperatureHighLabel) TextView mTemperatureHighLabel;
    @BindView(R.id.temperatureLowLabel) TextView mTemperatureLowLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.windDirectionValue) TextView mWindDirectionValue;
    @BindView(R.id.windValue) TextView mWindValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.uvIndexValue) TextView mUVIndexValue;
    @BindView(R.id.conditionImageView) ImageView mConditionImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_forecast);
        ButterKnife.bind(this);

        Drawable background = ContextCompat.getDrawable(this, MainActivity.BACKGROUND_ID);
        mDayForecastLayout.setBackground(background);

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra(DailyForecast.DAY_FORECAST);
        //FIX issue with the cast, just pass Day in as parameter in the constructor
        mDay = (Day) parcelable;

        updateDisplay();
    }

    private void updateDisplay(){
        mSummaryLabel.setText(mDay.getSummary());
        mTimeLabel.setText(mDay.getDayOfWeek());
        mConditionImageView.setImageResource(mDay.getIconId());
        mTemperatureHighLabel.setText(mDay.getHighTemperature() + "\u2109");
        mTemperatureLowLabel.setText(mDay.getLowTemperature() + "\u2109");
        mHumidityValue.setText(mDay.getHumidity() + "%");
        mPrecipValue.setText(mDay.getPrecipChance() + "%");
        mWindDirectionValue.setText(mDay.getWindDirection());
        mWindValue.setText(mDay.getWindSpeed() + "");
        mUVIndexValue.setText(mDay.getUVIndex() + "");
    }
}
