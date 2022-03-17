package com.example.globalweather.room.database.city

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.globalweather.model.constant.City

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upserts(cities: MutableList<City>)

    @Query("SELECT * FROM city_tbl ORDER BY id DESC")
    suspend fun getAllCity(): MutableList<City>?

    @Query("SELECT * FROM city_tbl WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String?): MutableList<City>


}