package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class ListDaily(
    @SerializedName("dt") val dt: Int? = null,
    @SerializedName("sunrise") val sunrise: Int? = null,
    @SerializedName("sunset") val sunset: Int? = null,
    @SerializedName("temp") val temp: Temp,
    @SerializedName("feels_like") val feels_like: Feels_like,
    @SerializedName("pressure") val pressure: Int? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("speed") val speed: Double? = null,
    @SerializedName("deg") val deg: Int? = null,
    @SerializedName("gust") val gust: Double? = null,
    @SerializedName("clouds") val clouds: Int? = null,
    @SerializedName("pop") val pop: Double? = null
)
