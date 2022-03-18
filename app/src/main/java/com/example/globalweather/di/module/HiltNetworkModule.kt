package com.example.globalweather.di.module

import com.example.globalweather.network.RetrofitBuilder
import com.example.globalweather.network.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltNetworkModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi =
        RetrofitBuilder.getRetrofit()
            .create(WeatherApi::class.java)


}