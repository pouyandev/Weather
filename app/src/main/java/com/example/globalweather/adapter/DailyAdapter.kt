package com.example.globalweather.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.globalweather.R
import com.example.globalweather.databinding.ItemDailyBinding
import com.example.globalweather.model.constant.ListDaily
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

        private val differCallback = object : DiffUtil.ItemCallback<ListDaily>() {
            override fun areItemsTheSame(oldItem: ListDaily, newItem: ListDaily): Boolean {
                return oldItem.dt == newItem.dt
            }

            override fun areContentsTheSame(oldItem: ListDaily, newItem: ListDaily): Boolean {
                return oldItem == newItem
            }

        }


    val differ = AsyncListDiffer(this, differCallback)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder.create(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.count()
    }

    class DailyViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(res: ListDaily) {
            binding.apply {
                res.run {
                    txtUpDaily.text = (temp.max!!.roundToInt() - 273).toString()
                    txtDownDaily.text = (temp.min!!.roundToInt() - 273).toString()
                    txtDateDaily.text = Instant.ofEpochSecond(dt!!.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("EEEE"))
                    when (weather[0].icon) {

                        "11d" -> {
                            imgIconDaily.setAnimation(R.raw.thunderstorm)
                        }

                        "11n" -> {
                            imgIconDaily.setAnimation(R.raw.thunderstorm)
                        }

                        "01n" -> {
                            imgIconDaily.setAnimation(R.raw.clear_sky_night)
                        }

                        "01d" -> {
                            imgIconDaily.setAnimation(R.raw.clear_sky)
                        }

                        "09d" -> {
                            imgIconDaily.setAnimation(R.raw.drizzle)
                        }

                        "09n" -> {
                            imgIconDaily.setAnimation(R.raw.drizzle)
                        }

                        "02d" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy)
                        }

                        "02n" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy_night)
                        }

                        "03d" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy)
                        }

                        "03n" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy_night)
                        }

                        "04d" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy)
                        }

                        "04n" -> {
                            imgIconDaily.setAnimation(R.raw.cloudy_night)
                        }

                        "10d" -> {
                            imgIconDaily.setAnimation(R.raw.rain)
                        }

                        "10n" -> {
                            imgIconDaily.setAnimation(R.raw.rain_night)
                        }

                        "13d" -> {
                            imgIconDaily.setAnimation(R.raw.snow)
                        }

                        "13n" -> {
                            imgIconDaily.setAnimation(R.raw.snow_night)
                        }

                        "50d" -> {
                            imgIconDaily.setAnimation(R.raw.mist)
                        }

                        "50n" -> {
                            imgIconDaily.setAnimation(R.raw.mist)
                        }

                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): DailyViewHolder {
                return DailyViewHolder(
                    ItemDailyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }
}