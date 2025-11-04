package com.example.weatherapp.ui

import com.example.weatherapp.data.model.WeatherData

/**
 * UI 狀態定義
 */
sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherData) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

