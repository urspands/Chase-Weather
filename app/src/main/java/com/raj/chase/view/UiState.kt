package com.raj.chase.view

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.WeatherResponse

sealed class UiState {
    object Loading : UiState()
    data class CitySearchSuccess(val cities: CitySearchResponse) : UiState()
    data class WeatherResponseSuccess(val weatherResponse: WeatherResponse) : UiState()
    data class Error(val message: String) : UiState()
}