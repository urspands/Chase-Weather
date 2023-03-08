package com.raj.chase.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raj.chase.R
import com.raj.chase.adapter.CityListAdapter
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.LocalNames
import com.raj.chase.api.WeatherResponse
import com.raj.chase.databinding.ActivityMainBinding
import com.raj.chase.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _viewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _fusedLocationClient: FusedLocationProviderClient

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            _viewModel.searchCity(p0?.toString()?.trim() ?: "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.progressCircular.visibility = View.GONE
        _binding.weatherConditionLayout.weatherCard.visibility = View.GONE
        _binding.citySearch.setOnFocusChangeListener { _, focus ->
            Log.d(TAG, "onCreate: City Search Focus::$focus")
            if (focus) {
                _binding.citySearch.addTextChangedListener(textChangeListener)
            }
        }
        _binding.currentLocation.setOnClickListener { getCurrentLocation() }
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val cityListAdapter =
            CityListAdapter { city ->
                _binding.citySearch.removeTextChangedListener(textChangeListener)
                (_binding.recyclerView.adapter as CityListAdapter).clearValues()
                _binding.citySearch.clearFocus()
                _viewModel.getWeatherConditionsForCity(city)
            }

        _binding.recyclerView.apply {
            adapter = cityListAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }

        _viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is UiState.CitySearchSuccess -> {
                    Log.d(TAG, "searchCity: response :: ${uiState.cities}")
                    cityListAdapter.setValues(uiState.cities)
                    showSearchResults(true)
                }
                is UiState.Error -> {
                    Log.e(
                        TAG,
                        "Result Error ${uiState.message}"
                    )
                    _binding.progressCircular.visibility = View.GONE
                    Toast.makeText(
                        this,
                        R.string.generic_error_message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                UiState.Loading -> {
                    _binding.progressCircular.visibility = View.VISIBLE
                }
                is UiState.WeatherResponseSuccess -> {
                    Log.d(
                        TAG,
                        "getWeatherConditionsForCity: response :: ${uiState.weatherResponse}"
                    )
                    bindWeatherData(uiState.weatherResponse)
                    showSearchResults(false)
                }
            }
        }
        _viewModel.currentCity.observe(this) { city ->
            _binding.citySearch.setText(getString(R.string.city_format, city.name, city.state))
        }

        _viewModel.loadLastKnownCityWeather()
    }

    private fun isLocationPermissionGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private fun getCurrentLocation() {
        if (isLocationPermissionGranted()) {
            getLocationAndLoadWeatherData()
        } else {
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndLoadWeatherData() {
        if (isLocationEnabled()) {
            _fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location == null) {
                    //TODO:: request for NewLocationData() is not implemented for this code challenge
                } else {
                    val city = getCityResponseItem(
                        location.latitude,
                        location.longitude
                    )
                    _viewModel.getWeatherConditionsForCity(city)
                }
            }
        } else {
            Toast.makeText(this, R.string.turn_on_location_msg, Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun getCityResponseItem(lat: Double, lon: Double): CitySearchResponseItem {
        val geocoder = Geocoder(this, Locale.getDefault())
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

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                        getLocationAndLoadWeatherData()
                    }
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun showSearchResults(show: Boolean) {
        _binding.recyclerView.visibility = if (show) View.VISIBLE else View.GONE
        _binding.weatherConditionLayout.weatherCard.visibility =
            if (!show) View.VISIBLE else View.GONE
        _binding.progressCircular.visibility = View.GONE

    }

    private fun bindWeatherData(weatherResponse: WeatherResponse) {
        _binding.weatherConditionLayout.weatherDescription.text =
            weatherResponse.weather[0].main
        _binding.weatherConditionLayout.temperature.text =
            getString(R.string.fahrenheit_format, weatherResponse.main.temp.toInt().toString())
        Glide.with(this).load(getIconUrl(weatherResponse.weather[0].icon))
            .placeholder(getProgress())
            .into(_binding.weatherConditionLayout.weatherImage)
    }

    private fun getProgress() = CircularProgressDrawable(this).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }




    override fun onStop() {
        Log.d(TAG, "onStop: ")
        _viewModel.saveCurrentCity()
        super.onStop()
    }

    companion object {
        const val TAG = "MainActivity"
        const val WEATHER_IMAGE_URL = "https://openweathermap.org/img/wn/"
        const val WEATHER_IMAGE_SIZE = "@2x"
        const val WEATHER_IMAGE_FORMAT = ".png"
        const val LOCATION_PERMISSION_ID = 16
        //TODO: Move this function to helper class
        fun getIconUrl(icon: String): String {
            return WEATHER_IMAGE_URL.plus(icon).plus(WEATHER_IMAGE_SIZE).plus(WEATHER_IMAGE_FORMAT)
        }
    }
}