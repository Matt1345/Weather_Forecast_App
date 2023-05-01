package com.example.weatherforecastapp.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherforecastapp.data.City
import com.example.weatherforecastapp.network.MainRepositoryAutofill
import com.example.weatherforecastapp.utils.Resource
import kotlinx.coroutines.Dispatchers


class SearchViewModel(private val mainRepositoryAutofill: MainRepositoryAutofill) : ViewModel() {

    private val _autocompleteFiller = MutableLiveData<ArrayList<City>>()
    val autocompleteFiller: LiveData<ArrayList<City>> = _autocompleteFiller


    fun getAutocomplete() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val data = mainRepositoryAutofill.getAutofill().body()?.data
            emit(Resource.success(data))

        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addAutofill(filler: ArrayList<City>){
        _autocompleteFiller.value = filler
    }
}