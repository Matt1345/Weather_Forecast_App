package com.example.weatherforecastapp.network

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getForecast() = apiHelper.getForecast()
}