package com.raj.chase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.raj.chase.api.*
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
import org.mockito.Mockito.never
import org.mockito.junit.MockitoJUnitRunner

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
    fun testEmptyResponseForCitySearch() {
//        val cityName = "test city"
//        val cityNameWithCountry = "$cityName, US"
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
//                DataRepoResult.Success(
//                    CitySearchResponse()
//                )
//            )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.uiState.observeForever(uiStateObserver)
//
//        viewModel.searchCity(cityName)
//        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }
//
//        Mockito.verify(uiStateObserver).onChanged(UiState.CitySearchSuccess(CitySearchResponse()))
//        viewModel.uiState.removeObserver(uiStateObserver)

    }

//    @Test
//    fun testSuccessResponseForCitySearch() {
//        val cityName = "Union city"
//        val cityNameWithCountry = "$cityName, US"
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
//                DataRepoResult.Success(
//                    getMockSuccessResponse()
//                )
//            )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.uiState.observeForever(uiStateObserver)
//
//        viewModel.searchCity(cityName)
//        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }
//
//        Mockito.verify(uiStateObserver)
//            .onChanged(UiState.CitySearchSuccess(getMockSuccessResponse()))
//        viewModel.uiState.removeObserver(uiStateObserver)
//
//    }

//    @Test
//    fun testFailureResponseForCitySearch() {
//        val cityName = "Union city"
//        val cityNameWithCountry = "$cityName, US"
//        val exception = Exception("Something Went wrong.")
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getCitySearchResults(cityNameWithCountry)).thenReturn(
//                DataRepoResult.Error(
//                    exception
//                )
//            )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.uiState.observeForever(uiStateObserver)
//
//        viewModel.searchCity(cityName)
//        runBlocking { Mockito.verify(repository).getCitySearchResults(cityNameWithCountry) }
//
//        Mockito.verify(uiStateObserver)
//            .onChanged(UiState.Error(exception.toString()))
//        viewModel.uiState.removeObserver(uiStateObserver)
//
//    }
//
//    @Test
//    fun testSuccessResponseForGettingWeatherConditions() {
//        val city = getMockCity()
//        val weatherResponse = getMockWeatherResponse()
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon))
//                .thenReturn(
//                    DataRepoResult.Success(
//                        weatherResponse
//                    )
//                )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.uiState.observeForever(uiStateObserver)
//
//        viewModel.getWeatherConditionsForCity(city)
//        runBlocking {
//            Mockito.verify(repository).getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon)
//        }
//
//        Mockito.verify(uiStateObserver)
//            .onChanged(UiState.WeatherResponseSuccess(weatherResponse))
//        viewModel.uiState.removeObserver(uiStateObserver)
//
//    }
//
//    @Test
//    fun testFailureResponseForGettingWeatherConditions() {
//        val city = getMockCity()
//        val exception = Exception("Something Went wrong.")
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon))
//                .thenReturn(
//                    DataRepoResult.Error(
//                        exception
//                    )
//                )
//        }
//
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.uiState.observeForever(uiStateObserver)
//
//        viewModel.getWeatherConditionsForCity(city)
//        runBlocking {
//            Mockito.verify(repository).getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon)
//        }
//
//        Mockito.verify(uiStateObserver)
//            .onChanged(UiState.Error(exception.toString()))
//        viewModel.uiState.removeObserver(uiStateObserver)
//
//    }
//
//    @Test
//    fun testSaveCurrentCityWhenCityIsNull() {
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.saveCurrentCity()
//        runBlocking {
//            Mockito.verifyNoInteractions(repository)
//        }
//    }
//
//    @Test
//    fun testSaveCurrentCityWhenCityIsNotNull() {
//        val city = getMockCity()
//        val weatherResponse = getMockWeatherResponse()
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon))
//                .thenReturn(
//                    DataRepoResult.Success(
//                        weatherResponse
//                    )
//                )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.getWeatherConditionsForCity(city)
//        viewModel.saveCurrentCity()
//
//        runBlocking {
//            Mockito.verify(repository).saveCityToPersistence(city)
//        }
//    }
//
//    @Test
//    fun testLoadLastKnownCityWeatherWhenCityIsNull() {
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getCityFromPersistence())
//                .thenReturn(
//                    null
//                )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.loadLastKnownCityWeather()
//        runBlocking {
//            val city = getMockCity()
//            Mockito.verify(repository, never()).getWeatherConditionsByLatLong(city.lat, city.lon)
//        }
//    }
//
//    @Test
//    fun testLoadLastKnownCityWeatherWhenCityIsNotNull() {
//        val city = getMockCity()
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getCityFromPersistence())
//                .thenReturn(
//                    city
//                )
//        }
//        val weatherResponse = getMockWeatherResponse()
//        testCoroutineRule.runBlockingTest {
//            Mockito.`when`(repository.getWeatherConditionsByLatLong(lat = city.lat, lon = city.lon))
//                .thenReturn(
//                    DataRepoResult.Success(
//                        weatherResponse
//                    )
//                )
//        }
//        val viewModel = MainActivityViewModel(repository)
//        viewModel.loadLastKnownCityWeather()
//        runBlocking {
//            Mockito.verify(repository).getWeatherConditionsByLatLong(city.lat, city.lon)
//        }
//    }
//
//
//    private fun getMockCity() = CitySearchResponseItem(
//        country = "US",
//        lat = 10.10,
//        lon = 11.11,
//        name = "Union City",
//        state = "CA",
//        local_names = LocalNames(en = "UnionCity")
//    )
//
//    private fun getMockWeatherResponse() = WeatherResponse(
//        base = "",
//        clouds = Clouds(100),
//        cod = 10,
//        coord = Coord(10.10, 11.11),
//        dt = 1,
//        id = 1232,
//        main = Main(0.0, 1, 2, 3, 4, 11.11, 12.10, 12.12),
//        name = "test",
//        sys = Sys("US", 23, 23, 23, 23),
//        timezone = 8,
//        visibility = 4,
//        weather = listOf(),
//        wind = Wind(8, 7.7, 6.6)
//
//    )
//
//    private fun getMockSuccessResponse(): CitySearchResponse {
//        val response1 = CitySearchResponseItem(
//            country = "US",
//            lat = 10.10,
//            lon = 11.11,
//            name = "Union City",
//            state = "CA",
//            local_names = LocalNames(en = "UnionCity")
//        )
//        val response2 = CitySearchResponseItem(
//            country = "US",
//            lat = 10.10,
//            lon = 11.11,
//            name = "Union City",
//            state = "IO",
//            local_names = LocalNames(en = "UnionCity")
//        )
//        return CitySearchResponse().apply {
//            add(response1)
//            add(response2)
//        }
//    }
}