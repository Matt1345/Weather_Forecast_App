package com.example.weatherforecastapp.network

import retrofit2.http.Query

class ApiHelperAutofill(private val apiService: ApiService, private val filler_name : String) {
    suspend fun getAutocomplete() = apiService.getAutocomplete(filler_name)
}