package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class ListDaily(
    @SerializedName("dt") val dt: Int,
    @SerializedName("sunrise") val sunrise: Int,
    @SerializedName("sunset") val sunset: Int,
    @SerializedName("temp") val temp: Temp,
    @SerializedName("feels_like") val feels_like: Feels_like,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int,
    @SerializedName("gust") val gust: Double,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("pop") val pop: Double
)
