package com.raj.chase.view

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.WeatherResponse

/**
 * This class is used to set the UI state in the viewModel using the liveData
 */
sealed class UiState {
    object Loading : UiState()
    data class CitySearchSuccess(val cities: CitySearchResponse) : UiState()
    data class WeatherResponseSuccess(val weatherResponse: WeatherResponse) : UiState()
    data class WeatherListResponseSuccess(val weatherList: List<WeatherResponse>) : UiState()
    data class Error(val message: String) : UiState()
}