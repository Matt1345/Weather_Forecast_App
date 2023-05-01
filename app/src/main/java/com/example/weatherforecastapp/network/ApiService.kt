package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.data.AutocompleteFiller
import com.example.weatherforecastapp.data.CompleteForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //forecast.json?key=31071caa1415402abbc141645230104&q=London&days=7&aqi=no&alerts=no

    @GET("forecast.json?key=31071caa1415402abbc141645230104&days=7&aqi=no&alerts=no")
    suspend fun getForecast(@Query("q") city: String): Response<CompleteForecast>

//    @GET("forecast.json?key=31071caa1415402abbc141645230104&q=Beijing&days=8&aqi=no&alerts=no")
//    suspend fun getForecast(): Response<CompleteForecast>

    @GET("search.json?key=31071caa1415402abbc141645230104")
    suspend fun getAutocomplete(@Query("q") input: String): Response<AutocompleteFiller>

}