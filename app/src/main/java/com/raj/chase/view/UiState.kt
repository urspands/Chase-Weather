package com.raj.chase.view

import com.raj.chase.api.CitySearchResponse

sealed class UiState {
    object Loading : UiState()
    data class CitySearchSuccess(val cities: CitySearchResponse) : UiState()
    data class Error(val message: String) : UiState()
}