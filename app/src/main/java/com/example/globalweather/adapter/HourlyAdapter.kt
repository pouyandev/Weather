package com.example.globalweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.example.globalweather.R
import com.example.globalweather.databinding.ItemHourlyBinding
import com.example.globalweather.model.constant.ListHourly
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ListHourly>() {
        override fun areItemsTheSame(oldItem: ListHourly, newItem: ListHourly): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ListHourly, newItem: ListHourly): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
     return HourlyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }

    class HourlyViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(res: ListHourly) {
            binding.apply {
                res.run {
                    txtTempHourly.text = (main.temp!!.roundToInt() - 273).toString() + " \u00B0"
                    val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
                    val output = SimpleDateFormat("h:mm a")
                    val date: Date = input.parse(dt_txt)
                    txtTimeHourly.text = output.format(date)
                    val dateListWeather: List<String> = dt_txt!!.split(" ")
                    txtDateHourly.text = dateListWeather[0]
                    when (weather[0].icon) {
                        "11d" -> {
                            imgIconHourly.setAnimation(R.raw.thunderstorm)
                        }

                        "11n" -> {
                            imgIconHourly.setAnimation(R.raw.thunderstorm)
                        }

                        "01n" -> {
                            imgIconHourly.setAnimation(R.raw.clear_sky_night)
                        }

                        "01d" -> {
                            imgIconHourly.setAnimation(R.raw.clear_sky)
                        }

                        "09d" -> {
                            imgIconHourly.setAnimation(R.raw.drizzle)
                        }

                        "09n" -> {
                            imgIconHourly.setAnimation(R.raw.drizzle)
                        }

                        "02d" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy)
                        }

                        "02n" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy_night)
                        }

                        "03d" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy)
                        }

                        "03n" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy_night)
                        }

                        "04d" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy)
                        }

                        "04n" -> {
                            imgIconHourly.setAnimation(R.raw.cloudy_night)
                        }

                        "10d" -> {
                            imgIconHourly.setAnimation(R.raw.rain)
                        }

                        "10n" -> {
                            imgIconHourly.setAnimation(R.raw.rain_night)
                        }

                        "13d" -> {
                            imgIconHourly.setAnimation(R.raw.snow)
                        }

                        "13n" -> {
                            imgIconHourly.setAnimation(R.raw.snow_night)
                        }

                        "50d" -> {
                            imgIconHourly.setAnimation(R.raw.mist)
                        }

                        "50n" -> {
                            imgIconHourly.setAnimation(R.raw.mist)
                        }
                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): HourlyViewHolder {
                return HourlyViewHolder(
                    ItemHourlyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            }
        }

    }
}