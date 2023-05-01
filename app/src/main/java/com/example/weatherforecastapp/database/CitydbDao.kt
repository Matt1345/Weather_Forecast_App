package com.example.weatherforecastapp.database

import androidx.room.*
import com.example.weatherforecastapp.data.City_dbclass
import kotlinx.coroutines.flow.Flow

//here we define all the function for modifying are database table
@Dao
interface CitydbDao {

    @Upsert
    suspend fun upsertCity(city: City_dbclass)

    @Delete
    suspend fun removeCityFromFavs(city: City_dbclass)

    @Query("SELECT * FROM cities")
    suspend fun getCitiesOrderedByName() : List<City_dbclass>
}