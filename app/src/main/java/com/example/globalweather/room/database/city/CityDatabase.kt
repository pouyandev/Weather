package com.example.globalweather.room.database.city

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.globalweather.model.constant.City
import com.example.globalweather.room.converter.CityConverter
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [City::class], version = 1, exportSchema = false)
@TypeConverters(CityConverter::class)
abstract class CityDatabase : RoomDatabase() {

    abstract fun getWeatherDao(): CityDao

    companion object {
        @Volatile
        private var instance: CityDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(
                context,
                CityDatabase::class.java,
                "city_db.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }


}