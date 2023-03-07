package com.raj.chase.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.raj.chase.R
import com.raj.chase.adapter.CityListAdapter
import com.raj.chase.databinding.ActivityMainBinding
import com.raj.chase.viewModel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _viewModel: MainActivityViewModel by viewModels()
    private lateinit var _binding: ActivityMainBinding

    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            TODO("Not yet implemented")
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            _viewModel.searchCity(p0?.toString()?.trim() ?: "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.citySearch.setOnFocusChangeListener { _, focus ->
            Log.d(TAG, "onCreate: City Search Focus::$focus")
            if (focus) {
                _binding.citySearch.addTextChangedListener(textChangeListener)
            }
        }
        val cityListAdapter =
            CityListAdapter { city ->
                _binding.citySearch.removeTextChangedListener(textChangeListener)
                (_binding.recyclerView.adapter as CityListAdapter).clearValues()
                _binding.citySearch.setText(getString(R.string.city_format, city.name, city.state))
                _binding.citySearch.clearFocus()
                _viewModel.getWeatherConditionsForCity(city)
            }

        _binding.recyclerView.apply {
            adapter = cityListAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }

        _viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is UiState.CitySearchSuccess -> cityListAdapter.setValues(uiState.cities)
                is UiState.Error -> Toast.makeText(
                    this,
                    R.string.generic_error_message,
                    Toast.LENGTH_LONG
                ).show()
                UiState.Loading -> {//TODO: Show loading if needed}
                }
            }
        }

    }

    companion object {
        const val TAG = "MainActivity"
    }
}