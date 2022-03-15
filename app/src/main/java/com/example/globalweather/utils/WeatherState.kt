package com.example.globalweather.utils

import com.example.globalweather.model.CurrentWeatherRes
import retrofit2.Response

sealed class WeatherState {
    class Success(val data: MutableList<Response<CurrentWeatherRes>>) : WeatherState()
    class Error(val error: String) : WeatherState()
    object Loading : WeatherState()
    object Empty : WeatherState()


}

