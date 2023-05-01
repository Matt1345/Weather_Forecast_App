package com.example.weatherforecastapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherforecastapp.data.City_dbclass

@Database(
    entities = [City_dbclass::class],
    version = 3
)
abstract class CitiesDatabase: RoomDatabase() {
    abstract fun dao() : CitydbDao
}