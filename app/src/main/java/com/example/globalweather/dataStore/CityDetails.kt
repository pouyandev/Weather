package com.example.globalweather.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.globalweather.utils.Constants
import com.example.globalweather.utils.Constants.PREFERENCES_KEY
import com.example.globalweather.utils.Constants.PREFERENCES_NAME
import kotlinx.coroutines.flow.map


class CityDetails(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(PREFERENCES_NAME)

    companion object {
        val CITYNAME = stringPreferencesKey(PREFERENCES_KEY)
    }


    suspend fun storeDetails( cityName: String) {
        context.datastore.edit {
            it[CITYNAME] = cityName
        }
    }

    fun getCityName() = context.datastore.data.map {
        it[CITYNAME] ?: ""
    }

}