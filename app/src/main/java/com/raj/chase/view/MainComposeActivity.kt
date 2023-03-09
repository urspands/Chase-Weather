package com.raj.chase.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.raj.chase.LocationHelper
import com.raj.chase.R
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.api.WeatherResponse
import com.raj.chase.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : ComponentActivity() {
    private val _viewModel: MainActivityViewModel by viewModels()
    private lateinit var _locationHelper: LocationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) { MainScreen(getString(R.string.enter_city)) }
        }
        _locationHelper = LocationHelper(this)
        _viewModel.loadLastKnownCityWeather()
    }

    override fun onStop() {
        Log.d(MainActivity.TAG, "onStop: ")
        _viewModel.saveCurrentCity()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.LOCATION_PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainComposeActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                        _locationHelper.getLocationAndLoadWeatherData() {
                            _viewModel.getWeatherConditionsForCity(it)
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    @Composable
    fun MainScreen(cityHint: String) {
        val inputValue = _viewModel.citySearchFieldState.observeAsState("")
        val uiState = _viewModel.uiState.observeAsState()
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            val padding = dimensionResource(R.dimen.default_margin)
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(start = padding, top = padding, end = padding),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextField(
                    value = inputValue.value,
                    onValueChange = {
                        _viewModel.onCitySearchTextChanged(it)
                    },
                    placeholder = { Text(text = cityHint) },
                    modifier = Modifier.fillMaxWidth(.85f)
                    )
                IconButton(
                    onClick = {
                        _locationHelper.getCurrentLocation {
                            _viewModel.getWeatherConditionsForCity(
                                it
                            )
                        }
                    },
                    modifier = Modifier.padding()
                        .then(Modifier.size(dimensionResource(R.dimen.location_icon_size)))
                        .then(Modifier.align(alignment = Alignment.CenterVertically)),

                    ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_mylocation),
                        contentDescription = stringResource(R.string.current_location_icon)
                    )
                }
            }
            when (uiState.value) {
                is UiState.CitySearchSuccess -> CitySearchList((uiState.value as UiState.CitySearchSuccess).cities)
                is UiState.Error -> {
                    Toast.makeText(
                        this@MainComposeActivity, stringResource(R.string.generic_error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                            .padding(padding)
                    )
                }
                is UiState.WeatherResponseSuccess -> {
                    WeatherCard((uiState.value as UiState.WeatherResponseSuccess).weatherResponse)
                }
                null -> {
//
                }
            }

        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun WeatherCard(weatherResponse: WeatherResponse) {
        val padding = dimensionResource(R.dimen.default_margin)
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(padding),
            elevation = 8.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(padding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(modifier = Modifier.wrapContentSize()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val painter =
                            rememberImagePainter(data = MainActivity.getIconUrl(weatherResponse.weather[0].icon))
                        Image(
                            painter = painter,
                            contentDescription = stringResource(R.string.weather_icon),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = weatherResponse.weather[0].main,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

                Text(
                    text = stringResource(
                        R.string.fahrenheit_format,
                        weatherResponse.main.temp.toInt()
                    ),
                    modifier = Modifier.wrapContentSize(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun CitySearchList(cities: List<CitySearchResponseItem>) {
        val padding = dimensionResource(R.dimen.default_margin)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(start = padding, end = padding)
        ) {
            items(items = cities) { city -> CityListItem(city) }
        }
    }

    @Composable
    fun CityListItem(city: CitySearchResponseItem) {
        TextButton(onClick = { _viewModel.getWeatherConditionsForCity(city) }) {
            Text(
                text = stringResource(R.string.city_format, city.name, city.state),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            )
        }

    }

    @Preview
    @Composable
    fun DefaultPreview() {
        MainScreen(getString(R.string.enter_city))
    }
}

