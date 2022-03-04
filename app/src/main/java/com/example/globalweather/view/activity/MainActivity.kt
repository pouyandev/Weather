package com.example.globalweather.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.globalweather.databinding.ActivityMainBinding
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.room.WeatherDao
import com.example.globalweather.viewModel.WeatherViewModel
import com.example.globalweather.viewModel.WeatherViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel

    @Inject
    lateinit var api: WeatherApi

    @Inject
    lateinit var dao: WeatherDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        viewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(WeatherRepository(api, dao))
        )[WeatherViewModel::class.java]
    }

}