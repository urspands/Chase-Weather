package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse

interface DataRepository {
    /** makes api call to server to get search results for the city name passed
     * @param city city to search
     * @return returns a list of cities
     */
    suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse>

}