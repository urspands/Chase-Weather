package com.raj.chase.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.repository.DataRepoResult
import com.raj.chase.repository.DataRepository
import com.raj.chase.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _uiState: MutableLiveData<UiState> = MutableLiveData()
    val uiState: LiveData<UiState> = _uiState

    fun getWeatherConditionsForCity(city: CitySearchResponseItem) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val weatherResponse =
                dataRepository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon)) {
                is DataRepoResult.Error -> {
                    _uiState.value = UiState.Error(weatherResponse.exception.toString())
                }
                is DataRepoResult.Success -> {
                    _uiState.value =
                        UiState.WeatherResponseSuccess(weatherResponse = weatherResponse.data)
                }
            }
        }
    }

    fun searchCity(cityName: String) {
        if (cityName.length >= SEARCH_MIN_LENGTH) {
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                when (val response =
                    dataRepository.getCitySearchResults("$cityName, $US_COUNTRY_CODE")) {
                    is DataRepoResult.Error -> {
                        _uiState.value = UiState.Error(response.exception.toString())
                        Log.e(TAG, "searchCity: Result Error ${response.exception}")
                    }
                    is DataRepoResult.Success -> _uiState.value =
                        UiState.CitySearchSuccess(response.data)
                }
            }
        }
    }

    companion object {
        const val SEARCH_MIN_LENGTH = 3
        const val US_COUNTRY_CODE = "US"
        const val TAG = "MainActivityViewModel"
    }
}