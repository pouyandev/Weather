package com.example.globalweather.model.constant

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all") val all: Int? = null
)
