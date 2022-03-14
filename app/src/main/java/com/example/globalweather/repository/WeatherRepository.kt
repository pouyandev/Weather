package com.example.globalweather.repository


import android.content.Context
import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.globalweather.di.application.HiltApplication
import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.favorite.FavoriteDao
import com.example.globalweather.room.entity.Favorite
import com.example.globalweather.utils.Constants
import com.example.globalweather.utils.Constants.PREFERENCES_NAME
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.observeOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val favoriteDao: FavoriteDao
) {
    suspend fun getCurrentData(city: String, appid: String) = flow { emit(api.getCurrentData(city, appid)) }.flowOn(IO)

    suspend fun getHourlyData(city: String, appid: String) = flow { emit(api.getHourlyData(city, appid)) }.flowOn(IO)

    suspend fun getDailyData(city: String, appid: String) = flow { emit(api.getDailyData(city, appid)) }.flowOn(IO)

    suspend fun getCities() = flow { emit(cityDao.getAllCity()) }.flowOn(IO)

    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)

    suspend fun searchCity(query: String?) = flow { emit(cityDao.search(query)) }.flowOn(IO)

    suspend fun searchFavoriteCity(query: String?) = flow { emit(favoriteDao.searchFavoriteCity(query)) }.flowOn(IO)

    suspend fun fInsert(favorite: Favorite) = favoriteDao.upsert(favorite)

    suspend fun getAllCityFavorite() = flow { emit(favoriteDao.getAllCityFavorite()) }.flowOn(IO)

    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)

}