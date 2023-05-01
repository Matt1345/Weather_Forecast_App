package com.example.weatherforecastapp.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.network.ApiHelperAutofill
import com.example.weatherforecastapp.network.MainRepositoryAutofill
import com.example.weatherforecastapp.ui.search.SearchViewModel

class ViewModelFactoryAutofill(private val apiHelperAutofill: ApiHelperAutofill) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(MainRepositoryAutofill(apiHelperAutofill)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}