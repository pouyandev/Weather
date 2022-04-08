package com.example.globalweather.di.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.globalweather.dataStore.CityDetails
import com.example.globalweather.utils.Constants.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        AppContext = applicationContext
        cityDetails = CityDetails


    }


    companion object {
        lateinit var AppContext: Context
        lateinit var cityDetails: CityDetails

    }

}