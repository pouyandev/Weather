package com.example.globalweather.room.database.city

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.globalweather.model.constant.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upserts(cities: MutableList<City>)

    @Query("SELECT * FROM city_tbl WHERE name LIKE '%' || :query || '%'")
    fun search(query: String?): Flow<MutableList<City>?>
}