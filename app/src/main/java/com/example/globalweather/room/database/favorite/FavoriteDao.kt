package com.example.globalweather.room.database.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.globalweather.room.entity.Favorite
@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: Favorite)

    @Query("SELECT * FROM favorite_tbl")
    suspend fun getAllCityFavorite(): MutableList<Favorite>?

}