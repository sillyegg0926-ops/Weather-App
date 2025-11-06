package com.example.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.WeatherState
import com.example.weatherapp.ui.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var allGranted = true
            permissions.entries.forEach {
                if (!it.value) {
                    allGranted = false
                }
            }
            if (allGranted) {
                viewModel.fetchWeather()
            } else {
                showError("需要位置權限才能獲取天氣資訊")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupClickListeners()

        // 檢查權限並獲取天氣
        checkPermissionsAndFetch()
    }

    private fun checkPermissionsAndFetch() {
        if (viewModel.hasLocationPermission()) {
            viewModel.fetchWeather()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupObservers() {
        viewModel.weatherState.observe(this, Observer { state ->
            when (state) {
                is WeatherState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.locationText.visibility = View.GONE
                    binding.errorText.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                    binding.refreshFab.visibility = View.GONE
                }
                is WeatherState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.locationText.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                    binding.refreshFab.visibility = View.VISIBLE
                    
                    updateWeatherDisplay(state.data)
                }
                is WeatherState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.locationText.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.VISIBLE
                    binding.refreshFab.visibility = View.GONE
                    binding.errorText.text = state.message
                }
            }
        })
    }

    private fun updateWeatherDisplay(data: com.example.weatherapp.data.model.WeatherData) {
        binding.temperatureText.text = getString(R.string.temperature_celsius, data.temperature.toInt())
        binding.conditionText.text = data.weatherDescription
        
        // 顯示位置名稱
        data.locationName?.let {
            binding.locationText.text = it
        }
    }

    private fun setupClickListeners() {
        binding.card.setOnClickListener {
            val weatherData = viewModel.weatherData.value
            if (weatherData != null) {
                val intent = Intent(this, WeatherDetailActivity::class.java).apply {
                    putExtra("weather_data", weatherData)
                }
                startActivity(intent)
            }
        }

        binding.retryButton.setOnClickListener {
            checkPermissionsAndFetch()
        }

        binding.refreshFab.setOnClickListener {
            if (viewModel.hasLocationPermission()) {
                viewModel.fetchWeather()
            } else {
                checkPermissionsAndFetch()
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}