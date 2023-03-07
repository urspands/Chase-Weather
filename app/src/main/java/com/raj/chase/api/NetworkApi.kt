package com.raj.chase.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
        const val API_KEY = "c34256ecc40ab39ac11b014bb11a9558"
        const val CITY_SEARCH = "geo/1.0/direct"
    }

    @GET(CITY_SEARCH)
    suspend fun searchCity(
        @Query("q") city: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = API_KEY
    ):CitySearchResponse
}

