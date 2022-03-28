package com.example.globalweather.room.database.favorite

import androidx.room.*
import com.example.globalweather.room.entity.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: Favorite?)

    @Query("SELECT * FROM favorite_tbl")
     fun getAllCityFavorite(): Flow<MutableList<Favorite>?>

    @Delete
    suspend fun deleteFavoriteCity(favorite: Favorite?)

}