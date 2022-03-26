package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class ListHourly(
    @SerializedName("dt") val dt: Int? = null,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("visibility") val visibility: Int? = null,
    @SerializedName("pop") val pop: Double? = null,
    @SerializedName("dt_txt") val dt_txt: String? = null

)
