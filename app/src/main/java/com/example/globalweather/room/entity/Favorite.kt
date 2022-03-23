package com.example.globalweather.room.entity

import android.accounts.AuthenticatorDescription
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.globalweather.model.constant.City
import com.example.globalweather.model.constant.Main
import com.example.globalweather.model.constant.Weather
import kotlinx.parcelize.Parcelize


@Entity(tableName = "favorite_tbl")
data class Favorite(
    @ColumnInfo(name = "Id")
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "City")
    val cityName: String?,
    @ColumnInfo(name = "Description")
    val description: String?,
    @ColumnInfo(name = "Icon")
    val weather: String?,
    @ColumnInfo(name = "Temp")
    val main: String?

)