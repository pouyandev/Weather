package com.example.globalweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
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
                txtCountryFavorite.text = favorite.countryName.toString()
                txtTempFavorite.text = favorite.main!!.toString()
                val condition = favorite.weather
                when (condition) {

                    "11d" -> { imgIconFavorite.setImageResource(R.drawable.thunderstorm) }

                    "11n" -> { imgIconFavorite.setImageResource(R.drawable.thunderstorm) }

                    "01n" -> { imgIconFavorite.setImageResource(R.drawable.clear_sky) }

                    "01d" -> { imgIconFavorite.setImageResource(R.drawable.clear_sky) }

                    "09d" -> { imgIconFavorite.setImageResource(R.drawable.drizzle) }

                    "09n" -> { imgIconFavorite.setImageResource(R.drawable.drizzle) }

                    "02d" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "02n" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "03d" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "03n" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "04d" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "04n" -> { imgIconFavorite.setImageResource(R.drawable.clouds) }

                    "10d" -> { imgIconFavorite.setImageResource(R.drawable.rain) }

                    "10n" -> { imgIconFavorite.setImageResource(R.drawable.rain) }

                    "13d" -> { imgIconFavorite.setImageResource(R.drawable.snow) }

                    "13n" -> { imgIconFavorite.setImageResource(R.drawable.snow) }

                    "50d" -> { imgIconFavorite.setImageResource(R.drawable.mist) }

                    "50n" -> { imgIconFavorite.setImageResource(R.drawable.mist) }

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