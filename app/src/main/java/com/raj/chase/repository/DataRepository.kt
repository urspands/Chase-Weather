package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.WeatherResponse

interface DataRepository {
    /** makes api call to server to get search results for the city name passed
     * @param city city to search
     * @return returns a list of cities
     */
    suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse>

    /** makes api call to server to get weather conditions for the lat and long passed
     * @param lat latitude of the city
     * @param lon longitude of the city
     * @return [DataRepoResult]
     */
    suspend fun getWeatherConditionsByLatLong(
        lat: Double,
        lon: Double
    ): DataRepoResult<WeatherResponse>

}