package com.example.globalweather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.globalweather.model.constant.City
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upserts(cities: MutableList<City>)

    @Query("SELECT * FROM city_tbl ORDER BY id DESC")
    suspend fun getAllCity(): MutableList<City>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(city: City)

    @Query("SELECT * FROM city_tbl WHERE name LIKE '%' || :query || '%'")
    fun search(query: String?): Flow<MutableList<City>>
}