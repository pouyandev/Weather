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
                    txtTempHourly.text = (main.temp.roundToInt() - 273).toString() + " \u00B0"
                    val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
                    val output = SimpleDateFormat("h:mm a")
                    val date: Date = input.parse(dt_txt)
                    txtTimeHourly.text = output.format(date)
                    val dateListWeather: List<String> = dt_txt.split(" ")
                    txtDateHourly.text = dateListWeather[0]
                    val condition = weather[0].icon

                    when (condition) {

                        "11d" -> { imgIconHourly.setImageResource(R.drawable.thunderstorm) }

                        "11n" -> { imgIconHourly.setImageResource(R.drawable.thunderstorm) }

                        "01n" -> { imgIconHourly.setImageResource(R.drawable.clear_sky) }

                        "01d" -> { imgIconHourly.setImageResource(R.drawable.clear_sky) }

                        "09d" -> { imgIconHourly.setImageResource(R.drawable.drizzle) }

                        "09n" -> { imgIconHourly.setImageResource(R.drawable.drizzle) }

                        "02d" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "02n" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "03d" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "03n" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "04d" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "04n" -> { imgIconHourly.setImageResource(R.drawable.clouds) }

                        "10d" -> { imgIconHourly.setImageResource(R.drawable.rain) }

                        "10n" -> { imgIconHourly.setImageResource(R.drawable.rain) }                        "10d" -> { imgIconHourly.setImageResource(R.drawable.rain) }

                        "13d" -> { imgIconHourly.setImageResource(R.drawable.snow) }

                        "13n" -> { imgIconHourly.setImageResource(R.drawable.snow) }

                        "50d" -> { imgIconHourly.setImageResource(R.drawable.mist) }

                        "50n" -> { imgIconHourly.setImageResource(R.drawable.mist) }
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