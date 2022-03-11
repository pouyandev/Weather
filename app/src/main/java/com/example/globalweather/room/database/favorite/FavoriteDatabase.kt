package com.example.globalweather.room.database.favorite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.globalweather.room.converter.FavoriteConverter
import com.example.globalweather.room.entity.Favorite
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [Favorite::class], version = 1, exportSchema = false)
@TypeConverters(FavoriteConverter::class)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun getFavoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: FavoriteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(@ApplicationContext context: Context) =
            Room.databaseBuilder(
                context,
                FavoriteDatabase::class.java,
                "favorite_db.db")
                .fallbackToDestructiveMigration()
                .build()
    }


}