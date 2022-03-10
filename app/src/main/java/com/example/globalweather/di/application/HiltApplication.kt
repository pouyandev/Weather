package com.example.globalweather.di.application

import android.app.Application
import android.content.Context
import com.example.globalweather.model.constant.City
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppContext = applicationContext
        cities = ArrayList()


    }


    companion object {
        lateinit var AppContext: Context
        lateinit var cities: MutableList<City>
    }

}