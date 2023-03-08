package com.raj.chase.repository

import android.content.Context
import com.google.gson.Gson
import com.raj.chase.AppApplication
import com.raj.chase.api.CitySearchResponseItem

/**
 * Singleton class to save and retrieve data from local preference
 */
object AppPreference {
    private val preferences =
        AppApplication.instance.getSharedPreferences("com.raj.chase", Context.MODE_PRIVATE)
    private const val CITY_ITEM = "CITY_ITEM"

    /** saves the city object to local preference
     * @param citySearchResponseItem CitySearchResponseItem to save
     */
    fun saveCity(citySearchResponseItem: CitySearchResponseItem) {
        preferences.edit().putString(CITY_ITEM, Gson().toJson(citySearchResponseItem)).apply()
    }

    /** gets the saved city from local preference if exists else returns null
     * @return [CitySearchResponseItem] or null
     */
    fun getCity(): CitySearchResponseItem? {
        val city = preferences.getString(CITY_ITEM, null)
        return city?.let { Gson().fromJson(city, CitySearchResponseItem::class.java) }
    }
}