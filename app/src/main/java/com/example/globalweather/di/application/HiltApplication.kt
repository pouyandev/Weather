package com.example.globalweather.di.application

import android.app.Application
import android.content.Context
import android.os.Handler
import com.example.globalweather.model.constant.City
import com.example.globalweather.network.WeatherApi
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.room.WeatherDao
import com.example.globalweather.room.WeatherDatabase

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppContext = applicationContext
        AppHandler = Handler(applicationContext.mainLooper)

         cities= ArrayList()


    }

    companion object {
        lateinit var AppContext: Context
        lateinit var AppHandler: Handler
        lateinit var cities: MutableList<City>
    }

}