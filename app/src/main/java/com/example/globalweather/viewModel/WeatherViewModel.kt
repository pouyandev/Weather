package com.example.globalweather.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.globalweather.di.application.HiltApplication.Companion.AppContext
import com.example.globalweather.model.constant.City
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.utils.Constants.API_KEY
import com.example.globalweather.utils.Constants.CITY_NAME
import com.example.globalweather.utils.Constants.JSON_FILE
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var job: Job? = null
    private lateinit var cities: MutableList<City>
    private val cityList: MutableLiveData<MutableList<City>> = MutableLiveData()


    val searchQuery = MutableStateFlow("")

    init {

    }


    fun getData() {
        job = viewModelScope.async(IO) {
            val inputStream = AppContext.assets.open(JSON_FILE)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charsets.UTF_8)

            cities = GsonBuilder().create()
                .fromJson(json, object : TypeToken<MutableList<City>>() {}.type)
        }


    }

    fun getAllCities() {
        job = viewModelScope.async(IO) {
            repository.getCities().collectLatest {
                cityList.postValue(it)
            }
        }
    }


    fun add(city: City) {
        job = viewModelScope.async(IO) {
            repository.upsert(city)
        }

    }

    private fun searchCity() = searchQuery.flatMapLatest {
        repository.searchCity(it)
    }

    fun searchCities() = searchCity().asLiveData(IO)

    suspend fun getCurrent() = repository.getCurrentData(CITY_NAME, API_KEY).asLiveData()

    suspend fun getHourly() = repository.getHourlyData(CITY_NAME, API_KEY).asLiveData()

    suspend fun getDaily() = repository.getDailyData(CITY_NAME, API_KEY).asLiveData()

    suspend fun getAllCity() = repository.getCities().asLiveData(IO)


    override fun onCleared() {
        super.onCleared()
        job!!.cancel()
        job = null
    }

}