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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaron.stormy.AlertDialogFragment;
import com.example.aaron.stormy.LocationDialogFragment;
import com.example.aaron.stormy.NetworkDialogFragment;
import com.example.aaron.stormy.R;
import com.example.aaron.stormy.model.ColorBook;
import com.example.aaron.stormy.model.CurrentWeather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private ColorBook mColorBook;
    private CurrentWeather mCurrentWeather;
    private String jsonData;
    private boolean canShowNext12Hours = false;
    private boolean canShowNext7Days = false;
    private double mLatitude, mLongitude;

    private GoogleApiClient mGoogleApiClient;
    private Geocoder mGeocoder;

    private TextView next12HoursTextView;
    private TextView next7HoursTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        next7HoursTextView = (TextView) findViewById(R.id.next7DaysButton);
        next12HoursTextView = (TextView) findViewById(R.id.next12HoursButton);
        Resources res = getResources();
        mColorBook = new ColorBook(res.getStringArray(R.array.colors));

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
                    // Use the text provided in the EditText View to retrieve the new latlng
                    String address = mLocationEditText.getText().toString();
                    Address newAddress = setLatLngFromStringAddress(address);
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
            }
        });

        next12HoursTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canShowNext12Hours) {
                    int color = mColorBook.getColor(mCurrentWeather.getColorId());
                    Intent intent = new Intent(MainActivity.this, Next12Hours.class);
                    intent.putExtra(getString(R.string.json_data), jsonData);
                    intent.putExtra(getString(R.string.bkg_color), color);
                    startActivity(intent);
                }
            }
        });


        next7HoursTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canShowNext7Days){
                    int color = mColorBook.getColor(mCurrentWeather.getColorId());
                    Intent intent = new Intent(MainActivity.this, Next7Days.class);
                    intent.putExtra(getString(R.string.json_data), jsonData);
                    intent.putExtra(getString(R.string.bkg_color), color);
                    startActivity(intent);
                }
            }
        });

        Log.d(TAG, "Main UI code is running");
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
                            mCurrentWeather = getCurrentDetails(jsonData);
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

    private void updateDisplay() {
        String address = getStringAddressFromLatLng();
        if(address != null){
            mLocationLabel.setText(address);
        }
        mCurrentWeatherLayout.setBackgroundColor(mColorBook.getColor(mCurrentWeather.getColorId()));
        mTemperatureLabel.setText(mCurrentWeather.getmTemperature() + "\u2109");
        mTimeLabel.setText("As of " + mCurrentWeather.getFormattedTime() + ", it is presently");
        mHumidityValue.setText(mCurrentWeather.getmHumidity() + "%");
        mPrecipValue.setText(mCurrentWeather.getmPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getmSummary());
        Drawable drawable = ContextCompat.getDrawable(this, mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mApparentTempValue.setText(mCurrentWeather.getApparentTemperature() + "\u2109");
        mWindValue.setText(mCurrentWeather.getWindSpeed() + " mph");
        mUVIndexValue.setText(mCurrentWeather.getUVIndex() + "");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "Forecast TimeZone from JSON: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTimeZone(timeZone);
        currentWeather.setmIcon(currently.getString("icon"));
        currentWeather.setmTime(currently.getLong("time"));
        currentWeather.setmTemperature(currently.getDouble("temperature"));
        currentWeather.setmHumidity(currently.getDouble("humidity"));
        currentWeather.setmPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setmSummary(currently.getString("summary"));
        currentWeather.setApparentTemperature(currently.getDouble("apparentTemperature"));
        currentWeather.setWindSpeed(currently.getDouble("windSpeed"));
        currentWeather.setUVIndex(currently.getInt("uvIndex"));

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void alertUserAboutNetworkAvailability(){
        NetworkDialogFragment dialog = new NetworkDialogFragment();
        dialog.show(getFragmentManager(), "network_dialog");
    }

    private void alertUserAboutLocationServiceAvailability(){
        LocationDialogFragment dialog = new LocationDialogFragment();
        dialog.show(getFragmentManager(), "location_dialog");
    }

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
                handleNewLocation(location);
                getForecast(mLatitude, mLongitude);
            }
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

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

    private Address setLatLngFromStringAddress(String address){
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
