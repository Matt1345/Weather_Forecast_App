package com.example.weatherforecastapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherforecastapp.data.CompleteForecast
import com.example.weatherforecastapp.network.MainRepository
import com.example.weatherforecastapp.utils.Resource
import kotlinx.coroutines.Dispatchers


class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _forecastComplete = MutableLiveData<CompleteForecast>()
    val forecastComplete: LiveData<CompleteForecast> = _forecastComplete


    fun getForecast() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val data = mainRepository.getForecast()
            emit(Resource.success(data))

        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addForecast(completeForecast: CompleteForecast){
        _forecastComplete.value = completeForecast
    }
}