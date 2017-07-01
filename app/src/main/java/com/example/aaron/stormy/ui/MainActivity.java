package com.example.aaron.stormy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.weather.ColorBook;
import com.example.aaron.stormy.weather.Current;
import com.example.aaron.stormy.weather.Day;
import com.example.aaron.stormy.weather.Forecast;
import com.example.aaron.stormy.weather.Hour;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static int BACKGROUND_ID = 0;
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private Forecast mForecast;
    private ColorBook mColorBook;
    private String jsonData;
    private boolean canShowNext12Hours = false;
    private boolean canShowNext7Days = false;
    private double mLatitude, mLongitude;

    private GoogleApiClient mGoogleApiClient;
    private Geocoder mGeocoder;

    private Button hourlyForecastButton;
    private Button dailyForecastButton;

    @BindView(R.id.current_weather_layout) ConstraintLayout mCurrentWeatherLayout;
    @BindView(R.id.locationEditText) EditText mLocationEditText;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.apparentTempValue) TextView mApparentTempValue;
    @BindView(R.id.windValue) TextView mWindValue;
    @BindView(R.id.uvIndexValue) TextView mUVIndexValue;
    @BindView(R.id.windDirectionValue) TextView mWindDirectionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        dailyForecastButton = (Button) findViewById(R.id.next7DaysButton);
        hourlyForecastButton = (Button) findViewById(R.id.next12HoursButton);
        Resources res = getResources();
        mColorBook = new ColorBook(this, res.getStringArray(R.array.backgrounds));

        mProgressBar.setVisibility(View.INVISIBLE);
        mGeocoder = new Geocoder(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        final double latitude = 37.8267;
        final double longitude = -122.4233;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationEditText.getText().toString().isEmpty()) {
                    // Continue with retrieving weather based off of current location
                    getForecast(mLatitude, mLongitude);
                }
                else{
                    getForecastFromProvidedLocation();
                }
            }
        });

        hourlyForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canShowNext12Hours) {
                    Current current = mForecast.getCurrentForecast();
                    int color = mColorBook.getBackgroudId(current.getColorId());
                    Intent intent = new Intent(MainActivity.this, HourlyForecast.class);
                    intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
                    startActivity(intent);
                }
            }
        });


        dailyForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canShowNext7Days){
                    Current current = mForecast.getCurrentForecast();
                    int color = mColorBook.getBackgroudId(current.getColorId());
                    Intent intent = new Intent(MainActivity.this, DailyForecast.class);
                    intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
                    startActivity(intent);
                }
            }
        });

        Log.d(TAG, "Main UI code is running");
    }

    private void getForecastFromProvidedLocation() {
        // Use the text provided in the EditText View to retrieve the new latlng
        String address = mLocationEditText.getText().toString();
        Address newAddress = getLatLngFromStringAddress(address);
        if(newAddress != null) {
            mLatitude = newAddress.getLatitude();
            mLongitude = newAddress.getLongitude();
            getForecast(mLatitude, mLongitude);
        }
        else{
            Toast.makeText(MainActivity.this, "Invalid Address provided. Please enter a valid address.", Toast.LENGTH_LONG);
            mLocationEditText.setText("");
            mLocationEditText.requestFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * This method will attempt to connect the network. If successful,
     * it will retreive the JSON data from the Dark Sky api
     * @param latitude
     * @param longitude
     */
    private void getForecast(double latitude, double longitude) {
        String apiKey = "90bd41bb2c697265aa5e510e80ef1bed";
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            canShowNext12Hours = true;
                            canShowNext7Days = true;
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mGoogleApiClient.isConnected()) {
                                        updateDisplay();
                                    }
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }else{
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
            alertUserAboutNetworkAvailability();
        }
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrentForecast(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for(int i = 0; i < data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setTime(jsonDay.getLong("time"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTimeZone(timezone);
            day.setHighTemperature(jsonDay.getDouble("temperatureMax"));
            day.setLowTemperature(jsonDay.getDouble("temperatureMin"));
            day.setPrecipChance(jsonDay.getDouble("precipProbability"));
            day.setUVIndex(jsonDay.getInt("uvIndex"));
            day.setHumidity(jsonDay.getDouble("humidity"));
            day.setWindDirection(jsonDay.getDouble("windBearing"));
            day.setWindSpeed(jsonDay.getDouble("windSpeed"));

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for(int i = 0; i < data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setTimeZone(timezone);
            hour.setTime(jsonHour.getLong("time"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setPrecipChance(jsonHour.getDouble("precipProbability"));
            hour.setIcon(jsonHour.getString("icon"));

            hours[i] = hour;
        }

        return hours;
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * updateDisplay() will update each of the Views with the appropriate
     * data contained in the Current object.
     */
    private void updateDisplay() {
        String address = getStringAddressFromLatLng();
        if(address != null){
            mLocationLabel.setText(address);
        }
        Current current = mForecast.getCurrentForecast();
        mTemperatureLabel.setText(current.getmTemperature() + "\u2109");
        mTimeLabel.setText("As of " + current.getFormattedTime() + ", it is presently");
        mHumidityValue.setText(current.getmHumidity() + "%");
        mPrecipValue.setText(current.getmPrecipChance() + "%");
        mSummaryLabel.setText(current.getmSummary());
        Drawable drawable = ContextCompat.getDrawable(this, current.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mApparentTempValue.setText(current.getApparentTemperature() + "\u2109");
        mWindValue.setText(current.getWindSpeed() + " mph");
        mWindDirectionValue.setText(current.getWindDirection());
        mUVIndexValue.setText(current.getUVIndex() + "");
        BACKGROUND_ID = mColorBook.getBackgroudId(current.getColorId());
        Drawable background = ContextCompat.getDrawable(this, BACKGROUND_ID);
        mCurrentWeatherLayout.setBackground(background);
    }

    /**
     * This method will parse the String jsonData which contains the JSON data
     * retrieved from the Dark Sky api
     * @param jsonData
     * @return currentWeather
     * @throws JSONException
     */
    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "Forecast TimeZone from JSON: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setTimeZone(timeZone);
        current.setmIcon(currently.getString("icon"));
        current.setmTime(currently.getLong("time"));
        current.setmTemperature(currently.getDouble("temperature"));
        current.setmHumidity(currently.getDouble("humidity"));
        current.setmPrecipChance(currently.getDouble("precipProbability"));
        current.setmSummary(currently.getString("summary"));
        current.setApparentTemperature(currently.getDouble("apparentTemperature"));
        current.setWindSpeed(currently.getDouble("windSpeed"));
        current.setUVIndex(currently.getInt("uvIndex"));
        current.setWindDirection(currently.getDouble("windBearing"));

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    /**
     * This method will check if the network is connected
     * @return isAvailable
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * Display dialog regarding an error
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * Display dialog regarding the network availability
     */
    private void alertUserAboutNetworkAvailability(){
        NetworkDialogFragment dialog = new NetworkDialogFragment();
        dialog.show(getFragmentManager(), "network_dialog");
    }

    /**
     * Display dialog regarding the location service availability
     */
    private void alertUserAboutLocationServiceAvailability(){
        LocationDialogFragment dialog = new LocationDialogFragment();
        dialog.show(getFragmentManager(), "location_dialog");
    }

    /**
     * This method checks if the proper permissions are granted for the app,
     * and if the permissions are not granted, it will display a dialog requesting them.
     * If so, the app will attempt to retrieve the last known location of the
     * device.
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1600);
        }else{
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location == null){
                Log.i(TAG, "LOCATION NOT FOUND.");
                alertUserAboutLocationServiceAvailability();

            }
            else{
                if(mLocationEditText.getText().toString().isEmpty()) {
                    handleNewLocation(location);
                    getForecast(mLatitude, mLongitude);
                }else{
                    getForecastFromProvidedLocation();
                }
            }
        }
    }

    /**
     * This method retrieves the longitude and latitude of the Location
     * object passed in as a parameter
     * @param location
     */
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    /**
     * This method will use the mLatitude and mLongitude values
     * to retrieve the address from the associated location using
     * the Geocoder. If successful, the address string will be set to
     * the city and state/country if available.
     * @return addressString or null
     */
    private String getStringAddressFromLatLng() {
        try {
            String city = "";
            String state = "";
            List<Address> addresses = mGeocoder.getFromLocation(mLatitude, mLongitude, 1);
            for (Address address : addresses) {
                city = address.getLocality();
                state = address.getAdminArea();

                //else state = address.getCountryName();
                Log.i(TAG, "City: " + city + ", State: " + state);

            }

            if(city == null || city.isEmpty()) city = "N/A";
            if(state == null || state.isEmpty()) {
                //state = "N/A";
                if(!addresses.isEmpty()) {
                    state = addresses.get(0).getCountryName();
                }
                else if(state == null || state.isEmpty()){
                    state = "N/A";
                }
            }

            String addressString = city + ", " + state;
            return addressString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method will return an Address using the Geocoder object
     * mGeocoder with the String address parameter.
     * @param address
     * @return addresses.get(0) or null
     */
    private Address getLatLngFromStringAddress(String address){
        try {
            List<Address> addresses = mGeocoder.getFromLocationName(address, 1);
            if(addresses.isEmpty()){
                return null;
            }else{
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage());

        }
    }
}
