package com.example.weatherforecastapp.data

import com.google.gson.annotations.SerializedName

data class AutocompleteFiller(
    val data: ArrayList<City>
)

data class City(
    @SerializedName("name")
    val name: String,
)