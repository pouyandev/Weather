package com.example.globalweather.utils

import com.example.globalweather.model.CurrentWeatherRes
import com.example.globalweather.model.ForecastDailyRes
import com.example.globalweather.model.ForecastHourlyRes
import com.example.globalweather.model.constant.City
import com.example.globalweather.room.entity.Favorite
import retrofit2.Response

sealed class WeatherState {
    class SuccessCurrent(val response: Response<CurrentWeatherRes>) : WeatherState()
    class SuccessDaily(val response: Response<ForecastDailyRes>) : WeatherState()
    class SuccessHourly(val response: Response<ForecastHourlyRes>) : WeatherState()
    class SearchCity(val response: MutableList<City>) : WeatherState()
    class SearchFavoriteCity(val response: MutableList<Favorite>) : WeatherState()
    class Error(val error: String?) : WeatherState()
    object Loading : WeatherState()
    object Empty : WeatherState()


}

