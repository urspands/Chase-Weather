package com.raj.chase.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.raj.chase.R
import com.raj.chase.adapter.CityListAdapter
import com.raj.chase.api.WeatherResponse
import com.raj.chase.databinding.ActivityMainBinding
import com.raj.chase.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _viewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: ActivityMainBinding

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


    private fun getIconUrl(icon: String): String {
        return WEATHER_IMAGE_URL.plus(icon).plus(WEATHER_IMAGE_SIZE).plus(WEATHER_IMAGE_FORMAT)
    }

    override fun onStop() {
        Log.d(TAG, "onStop: ")
        _viewModel.saveCurrentCity()
        super.onStop()
    }
    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }

    companion object {
        const val TAG = "MainActivity"
        const val WEATHER_IMAGE_URL = "https://openweathermap.org/img/wn/"
        const val WEATHER_IMAGE_SIZE = "@2x"
        const val WEATHER_IMAGE_FORMAT = ".png"
    }
}