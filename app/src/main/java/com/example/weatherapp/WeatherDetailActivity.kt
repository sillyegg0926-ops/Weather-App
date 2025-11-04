package com.example.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityWeatherDetailBinding

class WeatherDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayWeatherDetails()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun displayWeatherDetails() {
        val weatherData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("weather_data", com.example.weatherapp.data.model.WeatherData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<com.example.weatherapp.data.model.WeatherData>("weather_data")
        }
        
        if (weatherData != null) {
            binding.locationText.text = getString(R.string.temperature_celsius, weatherData.temperature.toInt())
            binding.conditionText.text = weatherData.weatherDescription
            
            binding.WindSpeed.text = getString(R.string.wind_speed_kmh, weatherData.windSpeed.toInt())
            binding.WindDirection.text = getString(R.string.wind_direction_degrees, weatherData.windDirection.toInt())
            
            if (weatherData.relativeHumidity != null) {
                binding.HumidityNumber.text = getString(R.string.humidity_percent, weatherData.relativeHumidity)
            } else {
                binding.HumidityNumber.text = getString(R.string.unknown_humidity)
            }
            
            if (weatherData.pressure != null) {
                binding.AirNumber.text = getString(R.string.pressure_hpa, weatherData.pressure.toInt())
            } else {
                binding.AirNumber.text = getString(R.string.unknown_pressure)
            }
            
            binding.LatitudeLongitude.text = getString(
                R.string.coordinates,
                weatherData.latitude,
                weatherData.longitude
            )
            
            binding.TimeText.text = getString(R.string.update_time, weatherData.time)
        }
    }
}

