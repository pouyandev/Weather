package com.example.globalweather.view.fragment

import android.annotation.SuppressLint
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
import androidx.fragment.app.viewModels
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
import kotlinx.coroutines.flow.map
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
    private val viewModel: WeatherViewModel by viewModels()
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


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun currentDetail(city: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
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
                            txtDateMain.text = Instant.ofEpochSecond(dt.toLong())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .format(
                                        DateTimeFormatter.ofPattern("EEE, MMM d")
                                    )

                            txtTemp.text = (main.temp!!.roundToInt() - 273).toString() + " \u00B0"
                            txtDescription.text = weather[0].description

                            txtFeelsLike.text =
                                (main.feels_like!!.roundToInt() - 273).toString() + " \u00B0"

                            binding.txtDownMain.text =
                                (main.temp_min!!.roundToInt() - 273).toString() + " \u00B0"

                            binding.txtUpMain.text =
                                (main.temp_max!!.roundToInt() - 273).toString() + " \u00B0"

                            val sunriseInstant: Instant =
                                Instant.ofEpochSecond(sys.sunrise!!.toLong())

                            val sunsetInstant: Instant =
                                Instant.ofEpochSecond(sys.sunset!!.toLong())

                            txtSunriseMain.text = formatTime(sunriseInstant)

                            txtSunsetMain.text = formatTime(sunsetInstant)

                            txtVisibilityMain.text = ((visibility)!! / 1000).toString() + " km/h"

                            txtHumidityMain.text = main.humidity.toString() + " %"

                            txtWindMain.text =
                                (((wind.speed)!! * 3.5).roundToInt()).toString() + " km/h"

                            txtPressureMain.text = main.pressure.toString() + " hPa"

                            val condition = weather[0].icon
                            when (condition) {

                                "11d" -> {
                                    imgIconMain.setAnimation(R.raw.thunderstorm)
                                }

                                "11n" -> {
                                    imgIconMain.setAnimation(R.raw.thunderstorm)
                                }

                                "01n" -> {
                                    imgIconMain.setAnimation(R.raw.clear_sky_night)
                                }

                                "01d" -> {
                                    imgIconMain.setAnimation(R.raw.clear_sky)
                                }

                                "09d" -> {
                                    imgIconMain.setAnimation(R.raw.drizzle)
                                }

                                "09n" -> {
                                    imgIconMain.setAnimation(R.raw.drizzle)
                                }

                                "02d" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy)
                                }

                                "02n" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy_night)
                                }

                                "03d" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy)
                                }

                                "03n" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy_night)
                                }

                                "04d" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy)
                                }

                                "04n" -> {
                                    imgIconMain.setAnimation(R.raw.cloudy_night)
                                }

                                "10d" -> {
                                    imgIconMain.setAnimation(R.raw.rain)
                                }

                                "10n" -> {
                                    imgIconMain.setAnimation(R.raw.rain_night)
                                }

                                "13d" -> {
                                    imgIconMain.setAnimation(R.raw.snow)
                                }

                                "13n" -> {
                                    imgIconMain.setAnimation(R.raw.snow_night)
                                }

                                "50d" -> {
                                    imgIconMain.setAnimation(R.raw.mist)
                                }

                                "50n" -> {
                                    imgIconMain.setAnimation(R.raw.mist)
                                }

                            }
                            favorite = Favorite(
                                id, name, weather[0].description,
                                condition,
                                (main.temp.roundToInt() - 273).toString() + " \u00B0"
                            )
                        }
                    }
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

    private fun forecastDailyDetail(city: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.handleDaily(city)
            viewModel.dailyData.collectLatest{
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