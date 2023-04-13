package com.raj.chase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.raj.chase.R
import com.raj.chase.api.WeatherResponse
import com.raj.chase.databinding.WeatherConditionLayoutBinding
import com.raj.chase.view.MainActivity

class WeatherAdapter(val onWeatherClick: (WeatherResponse) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    class WeatherViewHolder(private val binding: WeatherConditionLayoutBinding) :
        ViewHolder(binding.root) {
        fun bind(weatherResponse: WeatherResponse, onWeatherClick: (WeatherResponse) -> Unit) {
            Glide.with(binding.root.context)
                .load(MainActivity.getIconUrl(weatherResponse.weather[0].icon))
                .into(binding.weatherImage)
            binding.weatherDescription.text =
                weatherResponse.weather[0].main
            binding.temperature.text =
                binding.root.context.getString(
                    R.string.fahrenheit_format,
                    weatherResponse.main.temp.toInt().toString()
                )
            binding.cityName.text = weatherResponse.cityName ?: ""
            binding.weatherCard.setOnClickListener {
                onWeatherClick(
                    weatherResponse
                )
            }
        }
    }

    private val weatherList = ArrayList<WeatherResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            WeatherConditionLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = weatherList.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherList[position], onWeatherClick)
    }

    fun addItems(weatherResponse: List<WeatherResponse>) {
        weatherList.clear()
        weatherList.addAll(weatherResponse)
        notifyDataSetChanged()
    }
}