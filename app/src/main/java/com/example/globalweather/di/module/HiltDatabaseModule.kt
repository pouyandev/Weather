package com.example.globalweather.di.module

import android.content.Context
import com.example.globalweather.room.database.city.CityDao
import com.example.globalweather.room.database.city.CityDatabase
import com.example.globalweather.room.database.favorite.FavoriteDao
import com.example.globalweather.room.database.favorite.FavoriteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltDatabaseModule {

    @Provides
    @Singleton
    fun provideCityDatabase(@ApplicationContext context: Context): CityDatabase = CityDatabase.invoke(context)

    @Provides
    @Singleton
    fun provideFavoriteDatabase(@ApplicationContext context: Context): FavoriteDatabase = FavoriteDatabase.invoke(context)


    @Provides
    @Singleton
    fun provideCityDao(weatherDatabase: CityDatabase): CityDao = weatherDatabase.getWeatherDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(favoriteDatabase: FavoriteDatabase): FavoriteDao = favoriteDatabase.getFavoriteDao()
}