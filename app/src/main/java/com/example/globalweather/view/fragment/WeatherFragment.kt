package com.example.globalweather.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import coil.load
import coil.request.CachePolicy
import com.example.globalweather.R
import com.example.globalweather.adapter.DailyAdapter
import com.example.globalweather.adapter.HourlyAdapter
import com.example.globalweather.databinding.FragmentWeatherBinding
import com.example.globalweather.room.entity.Favorite
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var favorite: Favorite? = null
    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    private val hourlyAdapter by lazy { HourlyAdapter() }
    private val dailyAdapter by lazy { DailyAdapter() }
    private val searchArgs: SearchFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("h:mm a", Locale.ENGLISH)
        .withZone(ZoneId.systemDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        init()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        showLoading()
        currentDetail()
        forecastHourlyDetail()
        forecastDailyDetail()
        showAndHideFab()
        actionSearchAndFavorite()
    }

    private fun actionSearchAndFavorite() {

        binding.apply {
            imgSearchMain.setOnClickListener {
                findNavController().navigate(R.id.action_weatherFragment_to_searchFragment)
            }
            imgFavoriteMain.setOnClickListener {
                viewModel.addFavoriteCity(favorite!!)
                Snackbar.make(view, "Selected city added successfully", Snackbar.LENGTH_SHORT)
                    .show()
            }
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_weatherFragment_to_favoriteFragment)
            }
        }
    }

    private fun showAndHideFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.apply {
                scrollMain.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    val x = scrollY - oldScrollY
                    when {
                        x > 0 -> {
                            fab.show()
                        }
                        x < 0 -> {
                            fab.hide()
                        }
                        else -> {
                            fab.hide()
                        }
                    }
                }
            }
        }
    }

    private fun forecastDailyDetail() {
        lifecycleScope.launchWhenCreated {
            viewModel.getDaily(city = searchArgs.pCityName.name).collectLatest {
                hideLoading()
                initDailyRecyclerView()
                dailyAdapter.differ.submitList(it.list)
            }
        }

    }

    private fun forecastHourlyDetail() {
        lifecycleScope.launchWhenCreated {
            hideLoading()
            viewModel.getHourly(city = searchArgs.pCityName.name).collectLatest {
                hideLoading()
                initHourlyRecyclerView()
                hourlyAdapter.differ.submitList(it.list)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun currentDetail() {
        lifecycleScope.launchWhenCreated {
            hideLoading()
            viewModel.getCurrent(city = searchArgs.pCityName.name).collectLatest {
                binding.apply {
                    it.run {
                        txtCityName.text = name
                        txtDateMain.text =
                            Instant.ofEpochSecond(dt.toLong())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(
                                    DateTimeFormatter.ofPattern("EEE, MMM d")
                                )

                        txtTemp.text = (main.temp.roundToInt() - 273).toString() + " \u00B0"

                        txtDescription.text = weather[0].main
                        txtFeelsLike.text =
                            (main.feels_like.roundToInt() - 273).toString() + " \u00B0"

                        binding.txtDownMain.text =
                            (main.temp_min.roundToInt() - 273).toString() + " \u00B0"
                        binding.txtUpMain.text =
                            (main.temp_max.roundToInt() - 273).toString() + " \u00B0"

                        val sunriseInstant: Instant = Instant.ofEpochSecond(sys.sunrise.toLong())

                        val sunsetInstant: Instant = Instant.ofEpochSecond(sys.sunset.toLong())

                        txtSunriseMain.text = formatTime(sunriseInstant)

                        txtSunsetMain.text = formatTime(sunsetInstant)

                        txtVisibilityMain.text = ((visibility) / 1000).toString() + " km/h"

                        txtHumidityMain.text = main.humidity.toString() + " %"

                        txtWindMain.text = (((wind.speed) * 3.5).roundToInt()).toString() + " km/h"

                        txtPressureMain.text = main.pressure.toString() + " hPa"

                        val iconUrl = "http://openweathermap.org/img/w/" + weather[0].icon + ".png"

                        imgIconMain.load(iconUrl) {
                            crossfade(true)
                            crossfade(100)
                            allowConversionToBitmap(true)
                            diskCachePolicy(CachePolicy.ENABLED)
                            allowHardware(true)
                        }
                        favorite = Favorite(
                            searchArgs.pCityName.id,
                            searchArgs.pCityName.name,
                            searchArgs.pCityName.country,
                            "http://openweathermap.org/img/w/" + it.weather[0].icon + ".png",
                            (it.main.temp.roundToInt() - 273).toString() + " \u00B0"
                        )
                    }

                }
            }

        }
    }

    private fun initHourlyRecyclerView() {
        binding.rclForecastHourly.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), HORIZONTAL, false
            )
            adapter = hourlyAdapter
            hasFixedSize()
        }

    }

    private fun initDailyRecyclerView() {
        binding.rclForecastDaily.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dailyAdapter
            hasFixedSize()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(time: Instant): String? {
        return formatter.format(time)
    }

    private fun showLoading() {
        binding.prgMain.visibility = VISIBLE
    }

    private fun hideLoading() {
        binding.prgMain.visibility = INVISIBLE
    }

}