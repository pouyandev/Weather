package com.example.globalweather.di.module

import android.content.Context
import com.example.globalweather.room.WeatherDao
import com.example.globalweather.room.WeatherDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): WeatherDatabase =
        WeatherDatabase.invoke(context)


    @Provides
    @Singleton
    fun provideRoomDao(weatherDatabase: WeatherDatabase): WeatherDao =
        weatherDatabase.getWeatherDao()
}