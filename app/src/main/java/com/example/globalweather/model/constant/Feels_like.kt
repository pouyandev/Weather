package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class Feels_like(
    @SerializedName("day") val day: Double? = null,
    @SerializedName("night") val night: Double? = null,
    @SerializedName("eve") val eve: Double? = null,
    @SerializedName("morn") val morn: Double? = null
)
