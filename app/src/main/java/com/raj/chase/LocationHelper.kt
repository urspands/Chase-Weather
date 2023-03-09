package com.raj.chase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.LocalNames
import com.raj.chase.view.MainActivity
import java.util.*

/**
 * This is a helper class to get location permissions
 */
class LocationHelper(private val context: Activity) {
    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Gets the current location after all location permissions are satisfied and then executes the function passed to it
     * @param block which needs to be executed after the location permission is satisfied
     */
    fun getCurrentLocation(block: (city: CitySearchResponseItem) -> Unit) {
        if (isLocationPermissionGranted()) {
            getLocationAndLoadWeatherData {
                block(it)
            }
        } else {
            requestLocationPermissions()
        }
    }

    private fun isLocationPermissionGranted() =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            context, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), MainActivity.LOCATION_PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    fun getLocationAndLoadWeatherData(
        block: (city: CitySearchResponseItem) -> Unit
    ) {
        if (isLocationEnabled()) {
            _fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location == null) {
                    //TODO:: request for NewLocationData() is not implemented for this code challenge
                } else {
                    val city = getCityResponseItem(
                        location.latitude,
                        location.longitude, context
                    )
                    block(city)
                }
            }
        } else {
            Toast.makeText(context, R.string.turn_on_location_msg, Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

    private fun getCityResponseItem(
        lat: Double,
        lon: Double,
        context: Context
    ): CitySearchResponseItem {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
        val cityName: String = address!![0].locality
        val stateName: String = address[0].adminArea
        val countryName: String = address[0].countryName
        return CitySearchResponseItem(
            country = countryName,
            lat = lat,
            lon = lon,
            name = cityName,
            state = stateName,
            local_names = LocalNames(cityName)
        )
    }
}