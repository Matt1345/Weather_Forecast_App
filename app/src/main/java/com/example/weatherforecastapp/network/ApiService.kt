package com.example.weatherforecastapp.network

import com.example.weatherforecastapp.data.City
import com.example.weatherforecastapp.data.CompleteForecast
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "31071caa1415402abbc141645230104"
public interface ApiService {

    @GET("forecast.json?key=$API_KEY&days=7&aqi=no&alerts=no")
    suspend fun getForecast(@Query("q") city: String): Response<CompleteForecast>


    @GET("search.json?key=$API_KEY")
    suspend fun getAutocomplete(@Query("q") filler_name: String): Response<List<City>>

}