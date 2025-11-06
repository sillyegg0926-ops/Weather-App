package com.example.weatherapp.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * GPS 位置提供者
 */
class LocationProvider(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * 檢查位置權限是否已授予
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 獲取當前位置
     */
    suspend fun getCurrentLocation(): Result<Location> {
        return try {
            if (!hasLocationPermission()) {
                return Result.failure(Exception("位置權限未授予"))
            }
            
            val cancellationToken = CancellationTokenSource()
            val locationRequest = com.google.android.gms.location.CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(5000)
                .build()
            
            val location = fusedLocationClient.getCurrentLocation(
                locationRequest,
                cancellationToken.token
            ).await()
            
            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(Exception("無法獲取位置資訊"))
            }
        } catch (e: SecurityException) {
            Result.failure(Exception("位置權限被拒絕"))
        } catch (e: Exception) {
            Result.failure(Exception("獲取位置時發生錯誤: ${e.message}"))
        }
    }
    
    /**
     * 從座標獲取位置名稱
     */
    suspend fun getLocationName(latitude: Double, longitude: Double): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Use callback-based API for Android 13+ (API 33+)
                    suspendCancellableCoroutine<List<Address>> { continuation ->
                        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                            continuation.resume(addresses)
                        }
                    }
                } else {
                    // Use synchronous API for older Android versions (API 24-32)
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
                }
                
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = when {
                        !address.locality.isNullOrEmpty() -> address.locality
                        !address.subAdminArea.isNullOrEmpty() -> address.subAdminArea
                        !address.adminArea.isNullOrEmpty() -> address.adminArea
                        else -> String.format(Locale.getDefault(), "%.2f, %.2f", latitude, longitude)
                    }
                    Result.success(cityName)
                } else {
                    Result.success(String.format(Locale.getDefault(), "%.2f, %.2f", latitude, longitude))
                }
            } catch (e: Exception) {
                Result.success(String.format(Locale.getDefault(), "%.2f, %.2f", latitude, longitude))
            }
        }
    }
}

