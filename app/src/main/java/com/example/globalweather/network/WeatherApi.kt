package com.example.globalweather.network

import com.example.globalweather.model.CurrentWeatherRes
import com.example.globalweather.model.ForecastDailyRes
import com.example.globalweather.model.ForecastHourlyRes
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather?")
    suspend fun getCurrentData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): CurrentWeatherRes


    @GET("/data/2.5/forecast?")
    suspend fun getHourlyData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): ForecastHourlyRes


    @GET("/data/2.5/forecast/daily?")
    suspend fun getDailyData(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): ForecastDailyRes

}