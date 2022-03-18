package com.example.globalweather.room.database.favorite

import androidx.room.*
import com.example.globalweather.room.entity.Favorite
@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: Favorite?)

    @Query("SELECT * FROM favorite_tbl")
    suspend fun getAllCityFavorite(): MutableList<Favorite>?

    @Delete
    suspend fun deleteFavoriteCity(favorite: Favorite?)

    /*@Query("SELECT * FROM favorite_tbl WHERE City LIKE '%' || :query || '%'")
    suspend fun searchFavoriteCity(query: String?): MutableList<Favorite>?*/
}