package com.example.weather;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertWeather {

    private static final String TAG = ConvertWeather.class.getSimpleName();

    private static final String URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String CURRENT_WEATHER_TAG = "CURRENT_WEATHER";
    private static final String API_KEY = "Weather API Goes Here";

    private RequestQueue queue;

    public ConvertWeather(Activity activity) {
        queue = Volley.newRequestQueue(activity.getApplicationContext());
    }

    public interface CurrentWeatherCallback {

        void onCurrentWeather(GetWeather getWeather);

        void onError(Exception exception);
    }

    public void getCurrentWeather(String locationName, final CurrentWeatherCallback callback) {
        final String url = String.format("%s?q=%s&appId=%s", URL, locationName, API_KEY);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject currentWeatherJSONObject = new JSONObject(response);
                            final JSONArray weather = currentWeatherJSONObject.getJSONArray("weather");
                            final JSONObject weatherCondition = weather.getJSONObject(0);
                            final String locationName = currentWeatherJSONObject.getString("name");
                            final int conditionId = weatherCondition.getInt("id");
                            final String conditionName = weatherCondition.getString("main");
                            final double tempKelvin = currentWeatherJSONObject.getJSONObject("main").getDouble("temp");
                            final GetWeather getWeather = new GetWeather(locationName, conditionId, conditionName, tempKelvin);
                            callback.onCurrentWeather(getWeather);
                        } catch (JSONException e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                callback.onError(error);
            }
        });
        stringRequest.setTag(CURRENT_WEATHER_TAG);
        queue.add(stringRequest);
    }

    public void cancel() {
        queue.cancelAll(CURRENT_WEATHER_TAG);
    }
}
