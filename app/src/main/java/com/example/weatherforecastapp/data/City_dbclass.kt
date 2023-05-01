package com.example.weatherforecastapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity is basiclly table name
@Entity(tableName = "cities")
data class City_dbclass(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
) {
    override fun equals(other: Any?): Boolean {
        if (other is City_dbclass) {
            return cityName == other.cityName
        }
        return false
    }

    override fun hashCode(): Int {
        return cityName.hashCode()
    }
}
