package com.example.globalweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.globalweather.R
import com.example.globalweather.databinding.ItemFavoriteBinding
import com.example.globalweather.room.entity.Favorite

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    private val differCallback = object : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((Favorite) -> Unit)? = null
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

    fun setOnItemClickListener(listener: (Favorite) -> Unit) {
        onItemClickListener = listener
    }


    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(favorite: Favorite) {
            binding.apply {
                txtCityFavorite.text = favorite.cityName
                txtCountryFavorite.text = favorite.description.toString()
                txtTempFavorite.text = favorite.main!!.toString()
                when (favorite.weather) {

                    "11d" -> {
                        imgIconFavorite.setAnimation(R.raw.thunderstorm)
                    }

                    "11n" -> {
                        imgIconFavorite.setAnimation(R.raw.thunderstorm)
                    }

                    "01n" -> {
                        imgIconFavorite.setAnimation(R.raw.clear_sky_night)
                    }

                    "01d" -> {
                        imgIconFavorite.setAnimation(R.raw.clear_sky)
                    }

                    "09d" -> {
                        imgIconFavorite.setAnimation(R.raw.drizzle)
                    }

                    "09n" -> {
                        imgIconFavorite.setAnimation(R.raw.drizzle)
                    }

                    "02d" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy)
                    }

                    "02n" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy_night)
                    }

                    "03d" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy)
                    }

                    "03n" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy_night)
                    }

                    "04d" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy)
                    }

                    "04n" -> {
                        imgIconFavorite.setAnimation(R.raw.cloudy_night)
                    }

                    "10d" -> {
                        imgIconFavorite.setAnimation(R.raw.rain)
                    }

                    "10n" -> {
                        imgIconFavorite.setAnimation(R.raw.rain_night)
                    }

                    "13d" -> {
                        imgIconFavorite.setAnimation(R.raw.snow)
                    }

                    "13n" -> {
                        imgIconFavorite.setAnimation(R.raw.snow_night)
                    }

                    "50d" -> {
                        imgIconFavorite.setAnimation(R.raw.mist)
                    }

                    "50n" -> {
                        imgIconFavorite.setAnimation(R.raw.mist)
                    }

                }

            }
            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(favorite)
                }
            }

        }
    }
}