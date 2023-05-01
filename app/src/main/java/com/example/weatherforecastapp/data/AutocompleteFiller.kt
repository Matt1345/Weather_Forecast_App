package com.example.weatherforecastapp.data

import com.google.gson.annotations.SerializedName

//data class AutocompleteFiller(
//    val data: List<City>
//)

data class City(
    @SerializedName("name")
    val name: String
)