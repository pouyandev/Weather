package com.example.globalweather.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.globalweather.model.constant.City
import com.example.globalweather.room.converter.WeatherConverter
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [City::class], version = 1, exportSchema = false)
@TypeConverters(WeatherConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "weather_db.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }

}