package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open-Meteo API 服務介面
 */
interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,relativehumidity_2m,windspeed_10m,pressure_msl",
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>
}

