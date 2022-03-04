package com.example.globalweather.repository

import androidx.annotation.WorkerThread
import androidx.room.Query
import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.WeatherDao


import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) {

    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCities() = flow {emit(dao.getAllCity() ) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upserts(cities: MutableList<City>) = dao.upserts(cities)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upsert(city: City) =  dao.upsert(city)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun searchCity(query: String?) = dao.search(query)


}