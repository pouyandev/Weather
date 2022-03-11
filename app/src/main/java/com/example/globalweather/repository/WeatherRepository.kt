package com.example.globalweather.repository


import android.content.Context
import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.favorite.FavoriteDao
import com.example.globalweather.room.entity.Favorite
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao,
    private val favoriteDao: FavoriteDao
) {

/*    private val dataStore:DataStore<Preferences> = context.creada*/

    suspend fun getCurrentData(city: String, appid: String) =
        flow { emit(api.getCurrentData(city, appid)) }

    suspend fun getHourlyData(city: String, appid: String) =
        flow { emit(api.getHourlyData(city, appid)) }

    suspend fun getDailyData(city: String, appid: String) =
        flow { emit(api.getDailyData(city, appid)) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCities() = flow { emit(cityDao.getAllCity()) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun searchCity(query: String?) = flow { emit(cityDao.search(query)) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun fInsert(favorite: Favorite) = favoriteDao.upsert(favorite)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllCityFavorite() = flow { emit(favoriteDao.getAllCityFavorite()) }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)

}