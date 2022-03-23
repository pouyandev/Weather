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
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val favoriteDao: FavoriteDao) {
    
    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }.flowOn(IO)

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }.flowOn(IO)

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }.flowOn(IO)

    suspend fun getCities() = flow { emit(cityDao.getAllCity()) }.flowOn(IO)

    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)

    suspend fun searchCity(query: String?) = flow { emit(cityDao.search(query)) }.flowOn(IO)

    suspend fun favoriteUpsert(favorite: Favorite) = favoriteDao.upsert(favorite)

    suspend fun getAllCityFavorite() = flow { emit(favoriteDao.getAllCityFavorite()) }.flowOn(IO)

    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)

}