package com.raj.chase.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.raj.chase.R
import com.raj.chase.adapter.WeatherAdapter
import com.raj.chase.databinding.ActivityMultiCityBinding
import com.raj.chase.viewModel.MultiCityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultiCityActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMultiCityBinding
    private val _viewModel: MultiCityViewModel by viewModels()
    private lateinit var _weatherAdapter: WeatherAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMultiCityBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.etCitySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                _viewModel.onCitySearchTextChanged(p0.toString().trim())
            }
        })
        _weatherAdapter = WeatherAdapter { weatherResponse -> }
        _binding.weatherList.apply {
            adapter = _weatherAdapter
            layoutManager = LinearLayoutManager(this@MultiCityActivity)
            setHasFixedSize(false)
        }
        _viewModel.mediatorLiveData.observe(this) {
            when (it) {
                is UiState.CitySearchSuccess -> TODO()
                is UiState.Error -> Toast.makeText(
                    this@MultiCityActivity,
                    "Oops..Something went wrong.",
                    Toast.LENGTH_SHORT
                ).show()
                UiState.Loading -> TODO()
                is UiState.WeatherListResponseSuccess -> {
                    Log.d(TAG, "onCreate: ${it.weatherList.toString()}")
                    _weatherAdapter.addItems(it.weatherList)
                }
                is UiState.WeatherResponseSuccess -> TODO()
            }
        }
    }

    companion object {
        const val TAG = "MultiCityActivity"
    }
}