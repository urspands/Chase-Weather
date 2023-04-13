package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.WeatherResponse

interface DataRepository {
    suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse>
    suspend fun getWeatherConditionsByLatLong(
        lat: Double,
        lon: Double
    ): DataRepoResult<WeatherResponse>



    suspend fun saveCityToPersistence(city: CitySearchResponseItem)
    suspend fun getCityFromPersistence(): CitySearchResponseItem?
}