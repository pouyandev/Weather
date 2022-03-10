package com.example.globalweather.room.converter


import androidx.room.TypeConverter
import com.example.globalweather.model.constant.Coord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CityConverter {


    @TypeConverter
    fun tojson(coord: Coord): String? {
        val gson = Gson()
        val type = object : TypeToken<Coord>() {}.type
        return gson.toJson(coord, type)
    }

    @TypeConverter
    fun toDataClassCoord(coord: String?): Coord? {
        val gson = Gson()
        val type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(coord, type)
    }


}