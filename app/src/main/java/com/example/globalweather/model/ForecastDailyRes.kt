package com.example.globalweather.model

import com.example.globalweather.model.constant.City
import com.example.globalweather.model.constant.ListDaily
import com.google.gson.annotations.SerializedName

data class ForecastDailyRes(
    @SerializedName("city") val city: City,
    @SerializedName("cod") val cod: Int?=null,
    @SerializedName("message") val message: Double,
    @SerializedName("cnt") val cnt: Int?=null,
    @SerializedName("list") val list: MutableList<ListDaily>
)
