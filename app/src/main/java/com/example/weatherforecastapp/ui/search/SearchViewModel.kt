package com.example.weatherforecastapp.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherforecastapp.data.City
import com.example.weatherforecastapp.network.MainRepositoryAutofill
import com.example.weatherforecastapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse


class SearchViewModel(private val mainRepositoryAutofill: MainRepositoryAutofill) : ViewModel() {

    private val _autocompleteFiller = MutableLiveData<List<City>>()
    val autocompleteFiller: LiveData<List<City>> = _autocompleteFiller


    fun getAutocomplete() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val data : Response<List<City>> = mainRepositoryAutofill.getAutofill()
            emit(Resource.success(data))

        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addAutofill(cityList: List<City>){
        _autocompleteFiller.value = cityList
    }
}