package com.example.globalweather.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalweather.di.application.HiltApplication.Companion.AppContext
import com.example.globalweather.model.constant.City
import com.example.globalweather.repository.WeatherRepository
import com.example.globalweather.room.entity.Favorite
import com.example.globalweather.utils.Constants.API_KEY
import com.example.globalweather.utils.Constants.JSON_FILE
import com.example.globalweather.utils.WeatherState
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    init {
        convertJsonAndUpsert()
    }

    private lateinit var cities: MutableList<City>

    private val _currentData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val currentData = _currentData.asStateFlow()

    private val _hourlyData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val hourlyData = _hourlyData.asStateFlow()

    private val _dailyData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val dailyData = _dailyData.asStateFlow()

    private val _favoriteCities = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val favoriteCities = _favoriteCities.asStateFlow()

    private var _searchList = MutableStateFlow<WeatherState>(WeatherState.Empty)
    var searchList = _searchList.asStateFlow()

    //var searchQuery = ""

    private fun convertJsonAndUpsert() = viewModelScope.launch {
        json()
        repository.upserts(cities)
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

    }

    fun handleCurrentData(city: String) = viewModelScope.launch(IO) {
        _currentData.value = WeatherState.Loading
        repository.getCurrentData(city, API_KEY).catch {
            _currentData.value = WeatherState.Error(it.message)
        }.collectLatest {
            if (it.isSuccessful) {
                _currentData.value = WeatherState.SuccessCurrent(it)
            }
        }
    }


    fun handleHourly(city: String) = viewModelScope.launch(IO) {
        _hourlyData.value = WeatherState.Loading
        repository.getHourlyData(city, API_KEY).catch {
            _hourlyData.value = WeatherState.Error(it.message)
        }.collectLatest {
            if (it.isSuccessful) {
                _hourlyData.value = WeatherState.SuccessHourly(it)
            }
        }
    }

    fun handleDaily(city: String) = viewModelScope.launch(IO) {
        _dailyData.value = WeatherState.Loading
        repository.getDailyData(city, API_KEY).catch {
            _dailyData.value = WeatherState.Error(it.message)
        }.collectLatest {
            if (it.isSuccessful) {
                _dailyData.value = WeatherState.SuccessDaily(it)
            }
        }
    }


    fun addFavoriteCity(favorite: Favorite) = viewModelScope.launch {
        repository.favoriteUpsert(favorite)
    }


    fun deleteFavoriteCity(favorite: Favorite) = viewModelScope.launch {
        repository.deleteFavorite(favorite)
    }


    fun handleAllFavoriteCity() = viewModelScope.launch {
        _favoriteCities.value = WeatherState.Loading
        repository.getAllCityFavorite().catch {
            _favoriteCities.value = WeatherState.Error(it.message)
        }.collectLatest {
            _favoriteCities.value = WeatherState.SearchFavoriteCity(it!!)
        }
    }


     fun searchCity(searchQuery:String) = viewModelScope.launch {
        _searchList.value = WeatherState.Loading
        repository.searchCity(searchQuery).catch {
            _searchList.value = WeatherState.Error(it.message)
        }.collectLatest {
            _searchList.value = WeatherState.SearchQuery(it!!)
        }
    }


}