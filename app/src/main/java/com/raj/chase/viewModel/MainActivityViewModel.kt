package com.raj.chase.viewModel

import android.util.Log
import androidx.lifecycle.*
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
    val citySearchFieldState = MutableLiveData("")
    private val _uiCityState = citySearchFieldState.switchMap { searchCity(it.trim()) }
    private var _currentCity: MutableLiveData<CitySearchResponseItem> = MutableLiveData()
    private val _uiWeatherState = _currentCity.switchMap { getWeatherConditionsForCity(it) }
    val uiState: MediatorLiveData<UiState> = MediatorLiveData()

    val currentCity: LiveData<CitySearchResponseItem> = _currentCity

    init {
        uiState.addSource(_uiCityState) { city ->
            uiState.value = city
        }
        uiState.addSource(_uiWeatherState) { weather ->
            uiState.value = weather
        }
    }

    fun onCitySearchTextChanged(newText: String) {
        if (newText != citySearchFieldState.value) {
            citySearchFieldState.value = newText
//            searchCity(newText.trim())
        }
    }

    fun onCitySelected(city: CitySearchResponseItem) {
        _currentCity.value = city
    }

    /** loads the weather condition for the city
     * @param city CitySearchResponseItem
     */
    fun getWeatherConditionsForCity(city: CitySearchResponseItem): LiveData<UiState> {
        return liveData {
            emit(UiState.Loading)

            citySearchFieldState.value = city.name.plus(", ").plus(city.state)

            when (val weatherResponse =
                dataRepository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon)) {
                is DataRepoResult.Error -> {
                    emit(UiState.Error(weatherResponse.exception.toString()))
                }
                is DataRepoResult.Success -> {
                    emit(
                        UiState.WeatherResponseSuccess(weatherResponse = weatherResponse.data)
                    )
                }
            }

        }

    }

    /** Gets the matching geocoded city name in US
     *  @param cityName city name to search
     */
    fun searchCity(cityName: String): LiveData<UiState> {
        return liveData<UiState> {
            if (cityName.length >= SEARCH_MIN_LENGTH) {
                emit(UiState.Loading)
                when (val response =
                    dataRepository.getCitySearchResults("$cityName, $US_COUNTRY_CODE")) {
                    is DataRepoResult.Error -> {
                        emit(UiState.Error(response.exception.toString()))
                    }
                    is DataRepoResult.Success -> {
                        emit(
                            UiState.CitySearchSuccess(response.data)
                        )
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
                onCitySelected(it)
            }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: ")
        super.onCleared()
    }

    companion object {
        const val SEARCH_MIN_LENGTH = 3
        const val US_COUNTRY_CODE = "US"
        const val TAG = "MainActivityViewModel"
    }
}