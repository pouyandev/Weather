package com.example.globalweather.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalweather.di.application.HiltApplication
import com.example.globalweather.di.application.HiltApplication.Companion.AppContext
import com.example.globalweather.model.constant.City
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.room.entity.Favorite
import com.example.globalweather.utils.Constants.API_KEY
import com.example.globalweather.utils.Constants.COUNTRY_ID
import com.example.globalweather.utils.Constants.JSON_FILE
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var job: Job? = null
    private lateinit var cities: MutableList<City>


    val searchQuery = MutableStateFlow("")
    val searchFavoriteQuery = MutableStateFlow("")

    init {
        convertJsonAndUpsert()
    }


    fun convertJsonAndUpsert() {
        job = viewModelScope.async {
            if (repository.getCities() != HiltApplication.cities) {
                async { json() }.await()
                async { repository.upserts(HiltApplication.cities) }.await()
            }
        }

    }

    fun addFavoriteCity(favorite: Favorite) {
        job = viewModelScope.async {
            val add = async {
                repository.fInsert(favorite)
            }
            add.await()
        }
    }

    fun deleteFavoriteCity(favorite: Favorite){
        job = viewModelScope.async {
            val delete = async { repository.deleteFavorite(favorite) }
            delete.await()
        }
    }

    private fun json() {
        val inputStream = AppContext.assets.open(JSON_FILE)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, UTF_8)

        cities = GsonBuilder().create()
            .fromJson(json, object : TypeToken<MutableList<City>>() {}.type)

        for (city in cities) {
            if (city.country == COUNTRY_ID) {
                HiltApplication.cities.add(city)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun searchCity() = searchQuery.flatMapLatest {
        repository.searchCity(it)
    }

    @ExperimentalCoroutinesApi
    private fun searchFavoriteCity() = searchFavoriteQuery.flatMapLatest {
        repository.searchFavoriteCity(it)
    }



    @ExperimentalCoroutinesApi
    fun searchCities() = searchCity()

    @ExperimentalCoroutinesApi
    fun searchFavoriteCities() = searchFavoriteCity()

    suspend fun getCurrent(city: String) = repository.getCurrentData(city, API_KEY)

    suspend fun getHourly(city: String) = repository.getHourlyData(city, API_KEY)

    suspend fun getDaily(city: String) = repository.getDailyData(city, API_KEY)

    suspend fun getAllCity() = repository.getCities()

    suspend fun getAllFavoriteCity() = repository.getAllCityFavorite()

    override fun onCleared() {
        super.onCleared()
        job!!.cancel()
        job = null
    }

}