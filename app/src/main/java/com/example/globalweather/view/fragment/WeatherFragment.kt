package com.example.globalweather.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.globalweather.R
import com.example.globalweather.adapter.DailyAdapter
import com.example.globalweather.adapter.HourlyAdapter
import com.example.globalweather.databinding.FragmentWeatherBinding
import com.example.globalweather.di.application.HiltApplication
import com.example.globalweather.room.entity.Favorite
import com.example.globalweather.utils.WeatherState
import com.example.globalweather.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null

    private val binding get() = _binding!!

    private var favorite: Favorite? = null
    private val viewModel: WeatherViewModel by activityViewModels()
    private val hourlyAdapter by lazy { HourlyAdapter() }
    private val dailyAdapter by lazy { DailyAdapter() }
    lateinit var toggle: ActionBarDrawerToggle


    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("h:mm a", Locale.ENGLISH)
        .withZone(ZoneId.systemDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root


    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            HiltApplication.cityDetails.getCityName().collectLatest {
                if (it == "") {
                    findNavController().navigate(R.id.action_weatherFragment_to_searchFragment)
                }
                getData(it)
            }
        }
        actionDrawerLayout()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData(city: String) {
        currentDetail(city)
        forecastHourlyDetail(city)
        forecastDailyDetail(city)

    }

    private fun actionDrawerLayout() {
        toggle =
            ActionBarDrawerToggle(
                activity,
                binding.drawer,
                R.string.app_name,
                R.string.app_name
            )

        binding.apply {
            imgMenuMain.setOnClickListener {
                openDrawerLayout()
            }
            weatherNavigation.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.favoriteFragment -> {
                        viewModel.addFavoriteCity(favorite!!)
                        findNavController().navigate(R.id.action_weatherFragment_to_favoriteFragment)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun openDrawerLayout() {
        if (binding.drawer.isDrawerOpen(Gravity.LEFT)) {
            binding.drawer.closeDrawer(Gravity.LEFT)
            return
        }
        binding.drawer.openDrawer(Gravity.LEFT)
    }


    private fun forecastDailyDetail(city: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.handleDaily(city)
            viewModel.dailyData.collectLatest {
                when (it) {
                    is WeatherState.Loading -> showLoading()
                    is WeatherState.Error -> {
                        hideLoading()
                        Log.e("TAG", "forecastDailyDetail: ${it.error}")
                    }
                    is WeatherState.SuccessDaily -> {
                        hideLoading()
                        initDailyRecyclerView()
                        dailyAdapter.differ.submitList(it.response.body()!!.list)
                    }

                    else -> {}
                }
            }

        }

    }

    private fun forecastHourlyDetail(city: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.handleHourly(city)
            viewModel.hourlyData.collectLatest {
                when (it) {
                    is WeatherState.Loading -> showLoading()
                    is WeatherState.Error -> {
                        hideLoading()
                        Log.e("TAG", "forecastHourlyDetail: ${it.error}")
                    }
                    is WeatherState.SuccessHourly -> {
                        hideLoading()
                        initHourlyRecyclerView()
                        hourlyAdapter.differ.submitList(it.response.body()!!.list)
                    }
                    else -> {}
                }
            }
        }


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun currentDetail(city: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated{
            viewModel.handleCurrentData(city)
            viewModel.currentData.collectLatest {
                when (it) {
                    is WeatherState.Loading -> showLoading()
                    is WeatherState.Error -> {
                        hideLoading()
                        Log.e("TAG", "currentDetail: ${it.error}")
                    }
                    is WeatherState.SuccessCurrent -> {
                        hideLoading()
                        binding.apply {
                            it.response.body()!!.apply {
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

                                val sunriseInstant: Instant =
                                    Instant.ofEpochSecond(sys.sunrise.toLong())

                                val sunsetInstant: Instant =
                                    Instant.ofEpochSecond(sys.sunset.toLong())

                                txtSunriseMain.text = formatTime(sunriseInstant)

                                txtSunsetMain.text = formatTime(sunsetInstant)

                                txtVisibilityMain.text = ((visibility) / 1000).toString() + " km/h"

                                txtHumidityMain.text = main.humidity.toString() + " %"

                                txtWindMain.text =
                                    (((wind.speed) * 3.5).roundToInt()).toString() + " km/h"

                                txtPressureMain.text = main.pressure.toString() + " hPa"

                                val condition = weather[0].icon
                                when (condition) {


                                    "11d" -> { imgIconMain.setImageResource(R.drawable.thunderstorm) }

                                    "11n" -> { imgIconMain.setImageResource(R.drawable.thunderstorm) }

                                    "01n" -> { imgIconMain.setImageResource(R.drawable.clear_sky) }

                                    "01d" -> { imgIconMain.setImageResource(R.drawable.clear_sky) }

                                    "09d" -> { imgIconMain.setImageResource(R.drawable.drizzle) }

                                    "09n" -> { imgIconMain.setImageResource(R.drawable.drizzle) }

                                    "02d" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "02n" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "03d" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "03n" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "04d" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "04n" -> { imgIconMain.setImageResource(R.drawable.clouds) }

                                    "10d" -> { imgIconMain.setImageResource(R.drawable.rain) }

                                    "10n" -> { imgIconMain.setImageResource(R.drawable.rain) }

                                    "13d" -> { imgIconMain.setImageResource(R.drawable.snow) }

                                    "13n" -> { imgIconMain.setImageResource(R.drawable.snow) }

                                    "50d" -> { imgIconMain.setImageResource(R.drawable.mist) }

                                    "50n" -> { imgIconMain.setImageResource(R.drawable.mist) }

                                }
                                val iconUrl = condition
                                favorite = Favorite(
                                    id, name, sys.country,
                                    iconUrl,
                                    (main.temp.roundToInt() - 273).toString() + " \u00B0")
                                }
                            }
                    }

                    else -> {}
                }
            }


        }
    }


    private fun initHourlyRecyclerView() {
        binding.rclForecastHourly.apply {
            layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null


    }

}