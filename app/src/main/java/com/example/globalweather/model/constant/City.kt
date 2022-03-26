package com.example.globalweather.model.constant


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "city_tbl")
data class City(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String?=null,
    @SerializedName("coord") val coord: Coord,
    @SerializedName("country") val country: String?=null,
    @SerializedName("population") val population: Int? = null,
    @SerializedName("timezone") val timezone: Int? = null,
    @SerializedName("sunrise") val sunrise: Int? = null,
    @SerializedName("sunset") val sunset: Int? = null
)
