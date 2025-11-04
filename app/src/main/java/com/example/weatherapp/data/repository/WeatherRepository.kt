package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.RetrofitInstance
import com.example.weatherapp.data.model.HourlyForecast
import com.example.weatherapp.data.model.WeatherCode
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 天氣資料倉庫，處理 API 呼叫和錯誤處理
 */
class WeatherRepository {
    
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getWeather(latitude, longitude)
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = convertToWeatherData(response.body()!!)
                    Result.success(weatherData)
                } else {
                    Result.failure(
                        Exception("API 錯誤: ${response.code()} - ${response.message()}")
                    )
                }
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("無法連線到網路，請檢查您的網路連線"))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("連線逾時，請稍後再試"))
            } catch (e: Exception) {
                Result.failure(Exception("發生錯誤: ${e.message}"))
            }
        }
    }
    
    private fun convertToWeatherData(response: WeatherResponse): WeatherData {
        val current = response.currentWeather
        val hourly = response.hourly
        
        val hourlyForecast = hourly?.let {
            it.time.mapIndexed { index, time ->
                HourlyForecast(
                    time = time,
                    temperature = it.temperature_2m.getOrNull(index) ?: 0.0,
                    humidity = it.relativeHumidity2m?.getOrNull(index),
                    windSpeed = it.windSpeed10m?.getOrNull(index),
                    pressure = it.pressureMsl?.getOrNull(index)
                )
            }
        }
        
        return WeatherData(
            latitude = response.latitude,
            longitude = response.longitude,
            temperature = current.temperature,
            weatherCode = current.weathercode,
            weatherDescription = WeatherCode.getDescription(current.weathercode),
            windSpeed = current.windSpeed,
            windDirection = current.windDirection,
            time = current.time,
            relativeHumidity = hourly?.relativeHumidity2m?.firstOrNull(),
            pressure = hourly?.pressureMsl?.firstOrNull(),
            hourlyForecast = hourlyForecast
        )
    }
}

