package com.example.weatherforecastapp.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    private var INSTANCE: CitiesDatabase? = null

    fun getInstance(context: Context): CitiesDatabase {
        if (INSTANCE == null) {
            synchronized(CitiesDatabase::class) {
                if (INSTANCE == null) {
                    INSTANCE = buildRoomDB(context)
                }
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            CitiesDatabase::class.java,
            "dbcina"
        ).fallbackToDestructiveMigration()
            .build()

}