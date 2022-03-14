package com.example.globalweather.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
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
import com.example.globalweather.di.application.HiltApplication
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

    private lateinit var binding: FragmentWeatherBinding

    private var favorite: Favorite? = null
    private val viewModel: WeatherViewModel by activityViewModels()
    private val hourlyAdapter by lazy { HourlyAdapter() }
    private val dailyAdapter by lazy { DailyAdapter() }
  /*  private val searchArgs: SearchFragmentArgs by navArgs()*/
    private var animation: TranslateAnimation? = null

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
        lifecycleScope.launchWhenCreated {
            HiltApplication.cityDetails.getCityName().collect{
                getData(it)
            }
        }

        showAndHideFab()
        actionSearchAndFavorite()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData(city: String) {
        currentDetail(city)
        forecastHourlyDetail(city)
        forecastDailyDetail(city)
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
                            fabShow(fab)
                        }
                        else -> {
                            fabHide(fab)
                        }
                    }
                }
            }
        }
    }

    private fun fabShow(fab: View) {
        animation = TranslateAnimation(0f, 0f, 500f, 0f)
        animation!!.duration = 300
        fab.startAnimation(animation)
        fab.visibility = VISIBLE
    }

    private fun fabHide(fab: View) {
        animation = TranslateAnimation(0f, 0f, 0f, 500f)
        animation!!.duration = 300
        fab.startAnimation(animation)
        fab.visibility = INVISIBLE
    }

    private fun forecastDailyDetail(city: String) {
        lifecycleScope.launchWhenCreated {
            viewModel.getDaily(city = city).collectLatest { response ->
                if (response.isSuccessful) {
                    response.body()!!.run {
                        hideLoading()
                        initDailyRecyclerView()
                        dailyAdapter.differ.submitList(list)
                    }
                }

            }
        }

    }

    private fun forecastHourlyDetail(city: String) {
        lifecycleScope.launchWhenCreated {
            hideLoading()
            viewModel.getHourly(city = city).collectLatest { response ->
                if (response.isSuccessful) {
                    response.body()!!.run {
                        hideLoading()
                        initHourlyRecyclerView()
                        hourlyAdapter.differ.submitList(list)
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun currentDetail(city: String) {
        lifecycleScope.launchWhenCreated {
            hideLoading()
            viewModel.getCurrent(city = city).collectLatest { response ->
                binding.apply {
                    if (response.isSuccessful) {
                        response.body()!!.run {
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
                 /*           favorite = Favorite(
                                searchArgs.pCityName!!.id,
                                searchArgs.pCityName!!.name,
                                searchArgs.pCityName!!.country,
                                "http://openweathermap.org/img/w/" + weather[0].icon + ".png",
                                (main.temp.roundToInt() - 273).toString() + " \u00B0"
                            )*/
                        }
                    }


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

}