package com.example.globalweather.repository


import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.favorite.FavoriteDao
import com.example.globalweather.room.entity.Favorite
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val favoriteDao: FavoriteDao) {
    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }

    suspend fun getCities() = flow { emit(cityDao.getAllCity()) }.flowOn(IO)

    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)

    suspend fun searchCity(query: String?) = flow { emit(cityDao.search(query)) }
/*
    suspend fun searchFavoriteCity(query: String?) = flow { emit(favoriteDao.searchFavoriteCity(query)) }
*/
    suspend fun favoriteUpsert(favorite: Favorite) = favoriteDao.upsert(favorite)

    suspend fun getAllCityFavorite() = flow { emit(favoriteDao.getAllCityFavorite()) }

    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)

}