package com.example.practica12.src.core.hardware.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Enum para que el estado sea más claro y robusto
enum class GpsStatus {
    CHECKING,
    REAL,
    MOCK,
    NO_LOCATION,
    NO_PERMISSION
}

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _gpsStatus = mutableStateOf(GpsStatus.CHECKING)
    val gpsStatus: State<GpsStatus> = _gpsStatus

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun checkLocationStatus() {
        // Primero, verificar permisos
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _gpsStatus.value = GpsStatus.NO_PERMISSION
            return
        }

        // Obtener la última ubicación conocida
        val location: Location? = try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
            _gpsStatus.value = GpsStatus.NO_PERMISSION
            return
        }

        // Actualizar el estado basado en la ubicación
        _gpsStatus.value = when {
            location == null -> GpsStatus.NO_LOCATION
            location.isFromMockProvider -> GpsStatus.MOCK
            else -> GpsStatus.REAL
        }
    }
}