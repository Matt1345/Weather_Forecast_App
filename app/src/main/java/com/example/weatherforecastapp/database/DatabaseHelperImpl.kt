package com.example.weatherforecastapp.database

import com.example.weatherforecastapp.data.City_dbclass

class DatabaseHelperImpl(private val citiesDatabase: CitiesDatabase) : DatabaseHelper {

    override suspend fun getCities(): List<City_dbclass> = citiesDatabase.dao().getCitiesOrderedByName()

    override suspend fun insertCity(city: City_dbclass) {
        citiesDatabase.dao().upsertCity(city)
    }

    override suspend fun removeCity(city: City_dbclass) {
        citiesDatabase.dao().removeCityFromFavs(city)
    }


}