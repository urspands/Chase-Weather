package com.raj.chase.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.WeatherResponse
import com.raj.chase.repository.DataRepoResult
import com.raj.chase.repository.DataRepository
import com.raj.chase.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MultiCityViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val citySearchText: MutableLiveData<String> = MutableLiveData()
    private val citySearchResponse = citySearchText.switchMap { getCityResults(it) }
    private val weatherResponse = MutableLiveData<DataRepoResult<List<WeatherResponse>>>()
    private val _cityResponseList = ArrayList<CitySearchResponseItem>()

    val mediatorLiveData: MediatorLiveData<UiState> = MediatorLiveData<UiState>().apply {
        fun update() {
            citySearchResponse.value?.let {
                when (it) {
                    is DataRepoResult.Error -> value = UiState.Error(it.exception.toString())
                    is DataRepoResult.Success -> {
                        _cityResponseList.addAll(it.data)
                        getWeatherResults(it.data)

                    }
                }
            }

        }
        addSource(citySearchResponse) {
            update()
        }
        addSource(weatherResponse) {
            when (it) {
                is DataRepoResult.Error -> value = UiState.Error(it.exception.toString())
                is DataRepoResult.Success -> {
                    value = UiState.WeatherListResponseSuccess(processData(it.data))
                }
                else -> {
                    Log.e(TAG, "OOPS: somethins wrong")
                }
            }
        }

    }
    private val _exceptionHandler = CoroutineExceptionHandler { _, exception ->
        mediatorLiveData.value = UiState.Error(exception.toString())
    }

    private fun processData(results: List<WeatherResponse>): List<WeatherResponse> {
        val returnValue = ArrayList<WeatherResponse>()
        results.forEach { result ->

            val cities = _cityResponseList?.filter { city ->
                roundDouble(city.lat) == roundDouble(
                    result.coord.lat
                ) && roundDouble(city.lon) == roundDouble(result.coord.lon)
            }
            if (cities?.size!! > 0) {
                val city = cities.first()
                result.cityName = "${city.name}, ${city.state}"
            }


            returnValue.add(result)

        }


        return returnValue
    }

    private fun roundDouble(input: Double): Double = String.format("%.2f", input).toDouble()

    fun onCitySearchTextChanged(searchString: String) {
        if (searchString.length > 3) {
            citySearchText.value = searchString
        }
    }


    private fun getCityResults(city: String): LiveData<DataRepoResult<CitySearchResponse>> {
        return liveData(viewModelScope.coroutineContext + _exceptionHandler) {
            try {
                val response = dataRepository.getCitySearchResults(city)
                emit(response)
            }catch (e:IOException){
                emit(DataRepoResult.Error(e))
            }
            catch (e:Exception){
                emit(DataRepoResult.Error(e))
            }
//            emit(dataRepository.getCitySearchResults(city))
        }

    }

    private fun getWeatherResults(citySearchResponse: CitySearchResponse) {
        if (citySearchResponse.isNotEmpty()) {
            viewModelScope.launch(_exceptionHandler) {
                try {
                    val response = citySearchResponse.map {
                        withContext(Dispatchers.Default) {
                            dataRepository.getWeatherConditionsByLatLong(
                                it.lat,
                                it.lon
                            )
                        }
                    }
                    val result = ArrayList<WeatherResponse>()
                    response.forEach { weather ->
                        when (weather) {
                            is DataRepoResult.Error -> {
                                Log.e(TAG, "getWeatherResults: Error fetching weather")
                            }
                            is DataRepoResult.Success -> result.add(weather.data)
                        }
                    }
                    weatherResponse.value = (DataRepoResult.Success(result))
                } catch (e: Exception) {
                    Log.e(TAG, "getWeatherResults: ${e.message}")
                    weatherResponse.value = (DataRepoResult.Error(e))
                }

            }
        }
    }

    companion object {
        const val TAG = "MultiCityViewModel"
    }
}

sealed class WeatherResult {
    data class Error(val exception: Exception) : WeatherResult()
    data class Success(val data: List<DataRepoResult<WeatherResponse>>) : WeatherResult()
}