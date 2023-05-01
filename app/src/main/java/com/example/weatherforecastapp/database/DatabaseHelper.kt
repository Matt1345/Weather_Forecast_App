package com.example.weatherforecastapp.database

import com.example.weatherforecastapp.data.City_dbclass

interface DatabaseHelper {

    suspend fun getCities(): List<City_dbclass>

    suspend fun insertCity(city: City_dbclass)

    suspend fun removeCity(city: City_dbclass)

}