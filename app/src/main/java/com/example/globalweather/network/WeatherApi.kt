package com.example.globalweather.network

import com.example.globalweather.model.CurrentWeatherRes
import com.example.globalweather.model.ForecastDailyRes
import com.example.globalweather.model.ForecastHourlyRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather?")
   suspend fun getCurrentData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Response<CurrentWeatherRes>


    @GET("forecast?")
     suspend fun getHourlyData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Response<ForecastHourlyRes>


    @GET("forecast/daily?")
    suspend fun getDailyData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Response<ForecastDailyRes>

}