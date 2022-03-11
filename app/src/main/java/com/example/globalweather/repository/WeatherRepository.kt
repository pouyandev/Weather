package com.example.globalweather.repository


import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.globalweather.di.application.HiltApplication
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
    private val favoriteDao: FavoriteDao
) {

       /* private val dataStore: DataStore<Preferences> = HiltApplication.AppContext.createDataStore()*/

    @WorkerThread
    suspend fun getCurrentData(city: String, appid: String) =
        flow { emit(api.getCurrentData(city, appid)) }.flowOn(IO)


    @WorkerThread
    suspend fun getHourlyData(city: String, appid: String) =
        flow { emit(api.getHourlyData(city, appid)) }.flowOn(IO)


    @WorkerThread
    suspend fun getDailyData(city: String, appid: String) =
        flow { emit(api.getDailyData(city, appid)) }.flowOn(IO)


    @WorkerThread
    suspend fun getCities() = flow { emit(cityDao.getAllCity()) }.flowOn(IO)


    @WorkerThread
    suspend fun upserts(cities: MutableList<City>) = cityDao.upserts(cities)


    @WorkerThread
    suspend fun searchCity(query: String?) = flow { emit(cityDao.search(query)) }.flowOn(IO)


    @WorkerThread
    suspend fun fInsert(favorite: Favorite) = favoriteDao.upsert(favorite)


    @WorkerThread
    suspend fun getAllCityFavorite() = flow { emit(favoriteDao.getAllCityFavorite()) }.flowOn(IO)


    @WorkerThread
    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavoriteCity(favorite)

}