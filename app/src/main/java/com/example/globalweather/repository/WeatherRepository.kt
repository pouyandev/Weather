package com.example.globalweather.repository


import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.favorite.FavoriteDao
import com.example.globalweather.room.entity.Favorite
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val favoriteDao: FavoriteDao) {

    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }

    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)

    fun searchCity(query: String?) = cityDao.search(query)

    suspend fun favoriteUpsert(favorite: Favorite) = favoriteDao.upsert(favorite)

    fun getAllCityFavorite() = favoriteDao.getAllCityFavorite()

    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)



}