package com.raj.chase.api

data class CitySearchResponseItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)