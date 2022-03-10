package com.example.globalweather.room.converter

import androidx.room.TypeConverter
import com.example.globalweather.model.constant.Main
import com.example.globalweather.model.constant.Weather
import com.example.globalweather.model.constant.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteConverter {
    @TypeConverter
    fun tojson(city: City): String? {
        val gson = Gson()
        val type = object : TypeToken<City>() {}.type
        return gson.toJson(city, type)
    }

    @TypeConverter
    fun toCityDataClassCoord(city: String?): City? {
        val gson = Gson()
        val type = object : TypeToken<City>() {}.type
        return gson.fromJson(city, type)
    }

    @TypeConverter
    fun tojson(main: Main): String? {
        val gson = Gson()
        val type = object : TypeToken<Main>() {}.type
        return gson.toJson(main, type)
    }

    @TypeConverter
    fun toMainDataClassCoord(main: String?): Main? {
        val gson = Gson()
        val type = object : TypeToken<Main>() {}.type
        return gson.fromJson(main, type)
    }

    @TypeConverter
    fun tojson(weather: Weather): String? {
        val gson = Gson()
        val type = object : TypeToken<Weather>() {}.type
        return gson.toJson(weather, type)
    }

    @TypeConverter
    fun toMainDataClassWeather(weather: String?): Weather? {
        val gson = Gson()
        val type = object : TypeToken<Weather>() {}.type
        return gson.fromJson(weather, type)
    }


}