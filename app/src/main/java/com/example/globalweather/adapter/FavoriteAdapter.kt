package com.example.globalweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.globalweather.databinding.ItemCityBinding
import com.example.globalweather.databinding.ItemFavoriteBinding
import com.example.globalweather.model.constant.City

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    private val differCallback = object : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(
            ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(city: City) {
            binding.apply {
                txtCityFavorite.text =city.name + " , "
                txtCountryFavorite.text = city.country
        /*        txtTempFavorite.text = s.main.temp.toString()
                imgIconFavorite.load(s.weather.icon) {
                    crossfade(true)
                    crossfade(500)
                    crossfade(true)
                    allowConversionToBitmap(true)
                    diskCachePolicy(CachePolicy.ENABLED)
                    allowHardware(true)
                }*/

            }

        }
    }
}