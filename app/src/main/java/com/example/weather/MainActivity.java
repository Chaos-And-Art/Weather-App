package com.example.weather;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.ConvertWeather.CurrentWeatherCallback;

public class MainActivity extends AppCompatActivity {

    private ConvertWeather convertWeather;

    private LinearLayout weatherContainer;
    private ProgressBar progressBar;
    private TextView toolBar_title, temperature, location, weatherCondition;
    private ImageView conditionIcon;
    private EditText locationField;
    private ImageButton refreshButton;

    private boolean fetchingWeather = false;
    private int textCount = 0;
    private String currentLocation = "Seattle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        convertWeather = new ConvertWeather(this);

        weatherContainer = findViewById(R.id.weather_container);
        progressBar = findViewById(R.id.progress_bar);
        toolBar_title = findViewById(R.id.toolBar_title);
        temperature = findViewById(R.id.temperature);
        location = findViewById(R.id.location);
        weatherCondition = findViewById(R.id.weather_condition);
        conditionIcon = findViewById(R.id.condition_icon);
        locationField = findViewById(R.id.location_field);
        refreshButton = findViewById(R.id.refresh_button);

        locationField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                count = s.toString().trim().length();
                refreshButton.setImageResource(count == 0 ? R.drawable.ic_refresh : R.drawable.ic_search);
                textCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                if (textCount == 0) {
                    refreshWeather();
                } else {
                    searchForWeather(locationField.getText().toString());
                    locationField.setText("");
                }
            }
        });

        // Start a search for the default location
        searchForWeather(currentLocation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        convertWeather.cancel();
    }

    private void refreshWeather() {
        if (fetchingWeather) {
            return;
        }
        searchForWeather(currentLocation);
    }

    private void searchForWeather(String location) {
        toggleProgress(true);
        fetchingWeather = true;
        convertWeather.getCurrentWeather(location, currentWeatherCallback);
    }

    private void toggleProgress(final boolean showProgress) {
        weatherContainer.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private final CurrentWeatherCallback currentWeatherCallback = new CurrentWeatherCallback() {

        @Override
        public void onCurrentWeather(GetWeather getWeather) {
            currentLocation = getWeather.location;
            temperature.setText(String.valueOf(getWeather.getTempFahrenheit()));
            toolBar_title.setText(getWeather.location);
            location.setText(getWeather.location);
            weatherCondition.setText(getWeather.weatherCondition);
            conditionIcon.setImageResource(WeatherIcons.getWeatherIconResId
                    (getWeather.conditionId));
            toggleProgress(false);
            fetchingWeather = false;
        }

        @Override
        public void onError(Exception exception) {
            toggleProgress(false);
            fetchingWeather = false;
            Toast.makeText(MainActivity.this, "There was an error fetching weather, " +
                    "try again.", Toast.LENGTH_SHORT).show();
        }
    };
}
