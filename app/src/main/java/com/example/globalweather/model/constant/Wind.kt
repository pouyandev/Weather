package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class Wind(

    @SerializedName("speed") val speed: Double? = null,
    @SerializedName("deg") val deg: Int? = null,
    @SerializedName("gust") val gust: Double? = null
)