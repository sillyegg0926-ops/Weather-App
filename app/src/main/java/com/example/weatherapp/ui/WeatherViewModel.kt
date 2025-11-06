package com.example.weatherapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.location.LocationProvider
import kotlinx.coroutines.launch

/**
 * 天氣 ViewModel，管理 UI 狀態和協程
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = WeatherRepository()
    private val locationProvider = LocationProvider(application.applicationContext)
    
    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState
    
    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData
    
    /**
     * 獲取天氣資料
     */
    fun fetchWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            
            // 先獲取位置
            val locationResult = locationProvider.getCurrentLocation()
            
            locationResult.onSuccess { location ->
                // 獲取位置成功，呼叫 API
                val weatherResult = repository.getWeather(
                    location.latitude,
                    location.longitude
                )
                
                weatherResult.onSuccess { data ->
                    // 獲取位置名稱
                    val locationNameResult = locationProvider.getLocationName(location.latitude, location.longitude)
                    val locationName = locationNameResult.getOrNull()
                    
                    // 更新資料包含位置名稱
                    val updatedData = data.copy(locationName = locationName)
                    _weatherData.value = updatedData
                    _weatherState.value = WeatherState.Success(updatedData)
                }.onFailure { exception ->
                    _weatherState.value = WeatherState.Error(exception.message ?: "未知錯誤")
                }
            }.onFailure { exception ->
                _weatherState.value = WeatherState.Error(exception.message ?: "無法獲取位置")
            }
        }
    }
    
    /**
     * 檢查位置權限
     */
    fun hasLocationPermission(): Boolean {
        return locationProvider.hasLocationPermission()
    }
}

