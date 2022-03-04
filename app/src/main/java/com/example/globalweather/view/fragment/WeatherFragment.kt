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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import coil.load
import coil.request.CachePolicy
import com.example.globalweather.R
import com.example.globalweather.adapter.DailyAdapter
import com.example.globalweather.adapter.HourlyAdapter
import com.example.globalweather.databinding.FragmentWeatherBinding
import com.example.globalweather.viewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeatherFragment : Fragment() {


    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val hourlyAdapter by lazy { HourlyAdapter() }
    private val dailyAdapter by lazy { DailyAdapter() }
/*    val args: WeatherFragmentArgs by navArgs()*/

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        init()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        /*       val weather = args.parWeather*/
        currentDetail()
        forecastHourlyDetail()
        forecastDailyDetail()
        showLoading()

        binding.apply {
            imgSearchMain.setOnClickListener {
                findNavController().navigate(R.id.action_weatherFragment_to_searchFragment)
            }
            imgFavoriteMain.setOnClickListener {
                findNavController().navigate(R.id.action_weatherFragment_to_favoriteFragment)
            }
            fab.setOnClickListener {
                Snackbar.make(view, "Selected city added successfully", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun forecastDailyDetail() {
        lifecycleScope.launchWhenCreated {
            viewModel.getDaily().observe(viewLifecycleOwner) {
                hideLoading()
                initDailyRecyclerView()
                dailyAdapter.differ.submitList(it.list)
            }
        }

    }

    private fun forecastHourlyDetail() {
        lifecycleScope.launchWhenCreated {
            viewModel.getHourly().observe(viewLifecycleOwner) {
                hideLoading()
                initHourlyRecyclerView()
                hourlyAdapter.differ.submitList(it.list)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun currentDetail() {
        hideLoading()
        lifecycleScope.launchWhenCreated {
            viewModel.getCurrent().observe(viewLifecycleOwner) {
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
                            allowConversionToBitmap(true)
                            diskCachePolicy(CachePolicy.ENABLED)
                            allowHardware(true)
                        }

                    }

                }
            }

        }
    }

    private fun initHourlyRecyclerView() {
        binding.rclForecastHourly.apply {
            layoutManager = LinearLayoutManager(
                activity, HORIZONTAL, false
            )
            adapter = hourlyAdapter
            hasFixedSize()
        }

    }

    private fun initDailyRecyclerView() {
        binding.rclForecastDaily.apply {
            layoutManager = LinearLayoutManager(activity)
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