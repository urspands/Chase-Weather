package com.raj.chase.repository

import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.NetworkApi

class DataRepositoryImpl(private val networkApi: NetworkApi) : DataRepository {
    override suspend fun getCitySearchResults(city: String): DataRepoResult<CitySearchResponse> {
        return try {
            val response = networkApi.searchCity(city)
            DataRepoResult.Success(response)
        } catch (e: java.lang.Exception) {
            DataRepoResult.Error(e)
        }
    }


}