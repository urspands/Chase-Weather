package com.raj.chase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raj.chase.R
import com.raj.chase.api.CitySearchResponseItem
import com.raj.chase.databinding.ListItemBinding

/**
 * CityListAdapter is a recycler view adapter to list the cities
 * @param onCitySelected ()function to call on city list item click
 */
class CityListAdapter(private val onCitySelected: (CitySearchResponseItem) -> Unit) :
    RecyclerView.Adapter<CityListAdapter.ItemViewHolder>() {

    private val cityList = ArrayList<CitySearchResponseItem>()

    class ItemViewHolder(private val listItemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(listItemBinding.root) {

        fun bind(
            citySearchResponseItem: CitySearchResponseItem,
            onCitySelected: (CitySearchResponseItem) -> Unit
        ) {
            listItemBinding.cityName.text = listItemBinding.root.context.getString(
                R.string.city_format,
                citySearchResponseItem.name,
                citySearchResponseItem.state
            )
            listItemBinding.cityName.setOnClickListener { onCitySelected(citySearchResponseItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(cityList[position], onCitySelected)
    }

    override fun getItemCount(): Int = cityList.size

    fun setValues(cities: ArrayList<CitySearchResponseItem>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    fun clearValues(){
        cityList.clear()
        notifyDataSetChanged()
    }
}