package com.raj.chase.viewModel

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

/**
 * view model class for the Main activity and has functions to supports the operations for the MainActivity
 * @param dataRepository Data Repository object
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _uiState: MutableLiveData<UiState> = MutableLiveData()
    val uiState: LiveData<UiState> = _uiState
    private var _currentCity: MutableLiveData<CitySearchResponseItem> = MutableLiveData()
    val currentCity: LiveData<CitySearchResponseItem> = _currentCity

    val citySearchFieldState = MutableLiveData("")
    fun onCitySearchTextChanged(newText: String) {
        if (newText != citySearchFieldState.value) {
            citySearchFieldState.value = newText
            searchCity(newText.trim())
        }
    }

    /** loads the weather condition for the city
     * @param city CitySearchResponseItem
     */
    fun getWeatherConditionsForCity(city: CitySearchResponseItem) {
        _uiState.value = UiState.Loading
        _currentCity.value = city
        citySearchFieldState.value = city.name.plus(", ").plus(city.state)
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

    /** Gets the matching geocoded city name in US
     *  @param cityName city name to search
     */
    fun searchCity(cityName: String) {
        if (cityName.length >= SEARCH_MIN_LENGTH) {
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                when (val response =
                    dataRepository.getCitySearchResults("$cityName, $US_COUNTRY_CODE")) {
                    is DataRepoResult.Error -> {
                        _uiState.value = UiState.Error(response.exception.toString())
                    }
                    is DataRepoResult.Success -> {
                        _uiState.value =
                            UiState.CitySearchSuccess(response.data)
                    }
                }
            }
        }
    }

    /**
     * save the current City to the persistence for later retrieval.
     */
    fun saveCurrentCity() {
        viewModelScope.launch {
            _currentCity.value?.let {
                dataRepository.saveCityToPersistence(it)
            }
        }
    }

    /**
     * loads the last known city and its weather conditions.
     */
    fun loadLastKnownCityWeather() {
        viewModelScope.launch {
            val citySearchResponseItem = dataRepository.getCityFromPersistence()
            citySearchResponseItem?.let {
                getWeatherConditionsForCity(it)
            }
        }
    }

    companion object {
        const val SEARCH_MIN_LENGTH = 3
        const val US_COUNTRY_CODE = "US"
        const val TAG = "MainActivityViewModel"
    }
}