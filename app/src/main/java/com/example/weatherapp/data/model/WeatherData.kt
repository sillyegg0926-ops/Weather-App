package com.example.weatherapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 應用內部使用的天氣資料類別
 */
@Parcelize
data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val windSpeed: Double,
    val windDirection: Double,
    val time: String,
    val relativeHumidity: Int? = null,
    val pressure: Double? = null,
    val hourlyForecast: List<HourlyForecast>? = null,
    val locationName: String? = null
) : Parcelable

@Parcelize
data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val humidity: Int?,
    val windSpeed: Double?,
    val pressure: Double?
) : Parcelable

