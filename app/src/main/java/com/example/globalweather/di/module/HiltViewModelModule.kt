package com.example.globalweather.di.module

import com.example.globalweather.network.RetrofitBuilder
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.favorite.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object HiltViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideWeatherApi(): WeatherApi =
        RetrofitBuilder
            .getRetrofit()
            .create(WeatherApi::class.java)

    @Provides
    @ViewModelScoped
    fun provideWeatherRepository(
        api: WeatherApi,
        cityDao: CityDao,
        favoriteDao: FavoriteDao
    ): WeatherRepository = WeatherRepository(api, cityDao, favoriteDao)


}