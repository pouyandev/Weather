package com.example.globalweather.repository

import androidx.room.Query
import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.WeatherDao

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) {

    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }

    suspend fun getCities() = flow {emit(dao.getAllCity() ) }.flowOn(Default)

    suspend fun upserts(cities: MutableList<City>) = dao.upserts(cities)

    suspend fun upsert(city: City) =  dao.upsert(city)

    fun searchCity(query: String?) = dao.search(query)


}