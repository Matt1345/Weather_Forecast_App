package com.example.weatherforecastapp.network

import retrofit2.http.Query

class ApiHelper(private val apiService: ApiService, private val city : String) {
    suspend fun getForecast() = apiService.getForecast(city)
}