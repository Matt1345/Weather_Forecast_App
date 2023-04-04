package com.example.weatherforecastapp.network

class MainRepositoryAutofill(private val apiHelperAutofill: ApiHelperAutofill) {
    suspend fun getAutofill() = apiHelperAutofill.getAutocomplete()
}