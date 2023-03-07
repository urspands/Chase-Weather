package com.raj.chase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.raj.chase.api.CitySearchResponse
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.LocalNames
import com.raj.chase.repository.DataRepoResult
import com.raj.chase.repository.DataRepository
import com.raj.chase.view.UiState
import com.raj.chase.viewModel.MainActivityViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: DataRepository

    @Mock
    private lateinit var uiStateObserver: Observer<UiState>

    @Test
    fun testEmptyResponse() {
        val cityName = "test city"
        val cityNameWithCountry = "$cityName, US"
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
                DataRepoResult.Success(
                    CitySearchResponse()
                )
            )
        }
        val viewModel = MainActivityViewModel(repository)
        viewModel.uiState.observeForever(uiStateObserver)

        viewModel.searchCity(cityName)
        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }

        Mockito.verify(uiStateObserver).onChanged(UiState.CitySearchSuccess(CitySearchResponse()))
        viewModel.uiState.removeObserver(uiStateObserver)

    }

    @Test
    fun testSuccessResponse() {
        val cityName = "Union city"
        val cityNameWithCountry = "$cityName, US"
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
                DataRepoResult.Success(
                    getMockSuccessResponse()
                )
            )
        }
        val viewModel = MainActivityViewModel(repository)
        viewModel.uiState.observeForever(uiStateObserver)

        viewModel.searchCity(cityName)
        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }

        Mockito.verify(uiStateObserver)
            .onChanged(UiState.CitySearchSuccess(getMockSuccessResponse()))
        viewModel.uiState.removeObserver(uiStateObserver)

    }

    @Test
    fun testFailureResponse() {
        val cityName = "Union city"
        val cityNameWithCountry = "$cityName, US"
        val exception = Exception("Something Went wrong.")
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
                DataRepoResult.Error(
                    exception
                )
            )
        }
        val viewModel = MainActivityViewModel(repository)
        viewModel.uiState.observeForever(uiStateObserver)

        viewModel.searchCity(cityName)
        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }

        Mockito.verify(uiStateObserver)
            .onChanged(UiState.Error(exception.toString()))
        viewModel.uiState.removeObserver(uiStateObserver)

    }

    private fun getMockSuccessResponse(): CitySearchResponse {
        val response1 = CitySearchResponseItem(
            country = "US",
            lat = 10.10,
            lon = 11.11,
            name = "Union City",
            state = "CA",
            local_names = LocalNames(en = "UnionCity")
        )
        val response2 = CitySearchResponseItem(
            country = "US",
            lat = 10.10,
            lon = 11.11,
            name = "Union City",
            state = "IO",
            local_names = LocalNames(en = "UnionCity")
        )
        return CitySearchResponse().apply {
            add(response1)
            add(response2)
        }
    }
}