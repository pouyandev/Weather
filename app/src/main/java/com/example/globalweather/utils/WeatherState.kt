package com.example.globalweather.utils

import com.example.globalweather.model.CurrentWeatherRes
import com.example.globalweather.model.ForecastDailyRes
import com.example.globalweather.model.ForecastHourlyRes
import retrofit2.Response

sealed class WeatherState {
    class SuccessCurrent(val response: Response<CurrentWeatherRes>) : WeatherState()
    class SuccessDaily(val response: Response<ForecastDailyRes>) : WeatherState()
    class SuccessHourly(val response: Response<ForecastHourlyRes>) : WeatherState()
    class Error(val error: Throwable) : WeatherState()
    object Loading : WeatherState()
    object Empty : WeatherState()


}

