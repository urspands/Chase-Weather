package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.NetworkApi
import com.raj.chase.api.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class implements server and local persistence api functions.
 * @param networkApi NetworkApi object to make server api calls
 */
class DataRepositoryImpl(private val networkApi: NetworkApi) : DataRepository {
    /** makes api call to server to get search results for the city name passed
     * @param city city to search
     * @return returns a list of cities
     */
    override suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse> {

        return try {
            val response = networkApi.searchCity(city)
            DataRepoResult.Success(response)
        } catch (e: Exception) {
            DataRepoResult.Error(e)
        }
//        return DataRepoResult.Error(java.lang.RuntimeException("Raj Exception"))
    }

    /** makes api call to server to get weather conditions for the lat and long passed
     * @param lat latitude of the city
     * @param lon longitude of the city
     * @return [DataRepoResult]
     */
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


    /** save the city to local persistence
     * @param city city to save
     */
    override suspend fun saveCityToPersistence(city: CitySearchResponseItem) {
        withContext(Dispatchers.IO) {
            AppPreference.saveCity(city)
        }
    }

    /** gets the city from the persistence if the value is saved else returns null
     * @return [CitySearchResponseItem]
     */
    override suspend fun getCityFromPersistence(): CitySearchResponseItem? {
        return withContext(Dispatchers.IO) {
            AppPreference.getCity()
        }
    }


}