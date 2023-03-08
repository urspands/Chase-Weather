package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.NetworkApi
import com.raj.chase.api.WeatherResponse

class DataRepositoryImpl(private val networkApi: NetworkApi) : DataRepository {
    override suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse> {
        return try {
            val response = networkApi.searchCity(city)
            DataRepoResult.Success(response)
        } catch (e: java.lang.Exception) {
            DataRepoResult.Error(e)
        }
    }

    override suspend fun getWeatherConditionsByLatLong(
        lat: Double,
        lon: Double
    ): DataRepoResult<WeatherResponse> {
        return try {
            val response = networkApi.getWeatherDetails(lat = lat, lon = lon)
            DataRepoResult.Success(response)
        } catch (e: java.lang.Exception) {
            DataRepoResult.Error(e)
        }
    }


}