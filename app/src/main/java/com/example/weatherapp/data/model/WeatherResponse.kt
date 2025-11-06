package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Open-Meteo API 回應結構
 */
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationTimeMs: Double,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather,
    @SerializedName("hourly")
    val hourly: HourlyWeather? = null
)

data class CurrentWeather(
    val temperature: Double,
    @SerializedName("windspeed")
    val windSpeed: Double,
    @SerializedName("winddirection")
    val windDirection: Double,
    val weathercode: Int,
    val time: String
)

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>,
    @SerializedName("relativehumidity_2m")
    val relativeHumidity2m: List<Int>? = null,
    @SerializedName("windspeed_10m")
    val windSpeed10m: List<Double>? = null,
    @SerializedName("pressure_msl")
    val pressureMsl: List<Double>? = null
)

