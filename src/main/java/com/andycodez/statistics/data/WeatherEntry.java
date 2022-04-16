package com.andycodez.statistics.data;

import java.time.LocalDateTime;

public class WeatherEntry {
    private LocalDateTime localDateTime;
    private float barometricPressure;

    public WeatherEntry() {
    }

    public WeatherEntry(LocalDateTime localDateTime, float barometricPressure) {
        this.localDateTime = localDateTime;
        this.barometricPressure = barometricPressure;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public float getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(float barometricPressure) {
        this.barometricPressure = barometricPressure;
    }
}
