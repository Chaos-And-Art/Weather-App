package com.example.weather;

public class GetWeather {
    final String location;
    final int conditionId;
    final String weatherCondition;
    final double tempKelvin;

    public GetWeather(final String location,
                      final int conditionId,
                      final String weatherCondition,
                      final double tempKelvin) {
        this.location = location;
        this.conditionId = conditionId;
        this.weatherCondition = weatherCondition;
        this.tempKelvin = tempKelvin;
    }

    public int getTempFahrenheit() {
        return (int) (tempKelvin * 9/5 - 459.67);
    }
}
