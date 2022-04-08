package com.example.globalweather.model

import com.example.globalweather.model.constant.City
import com.example.globalweather.model.constant.ListHourly
import com.google.gson.annotations.SerializedName


data class ForecastHourlyRes(

    @SerializedName("cod") val cod: Int? = null,
    @SerializedName("message") val message: Int? = null,
    @SerializedName("cnt") val cnt: Int? = null,
    @SerializedName("list") val list: MutableList<ListHourly>,
    @SerializedName("city") val city: City

)


