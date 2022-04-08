package com.example.globalweather.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalweather.di.application.HiltApplication.Companion.AppContext
import com.example.globalweather.model.CurrentWeatherRes
import com.example.globalweather.model.ForecastDailyRes
import com.example.globalweather.model.ForecastHourlyRes
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
import retrofit2.Response
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    init {
        convertJsonAndUpsert()
    }

    private lateinit var cities: MutableList<City>

    private val _currentData by lazy { MutableSharedFlow<WeatherState<Response<CurrentWeatherRes>>>() }
    val currentData by lazy { _currentData.asSharedFlow() }

    private val _hourlyData by lazy { MutableSharedFlow<WeatherState<Response<ForecastHourlyRes>>>() }
    val hourlyData by lazy { _hourlyData.asSharedFlow() }

    private val _dailyData by lazy { MutableSharedFlow<WeatherState<Response<ForecastDailyRes>>>() }
    val dailyData by lazy { _dailyData.asSharedFlow() }

    private val _favoriteCities by lazy { MutableSharedFlow<WeatherState<MutableList<Favorite>>>() }
    val favoriteCities by lazy { _favoriteCities.asSharedFlow() }

    private val _searchList by lazy { MutableSharedFlow<WeatherState<MutableList<City>>>() }
    val searchList by lazy { _searchList.asSharedFlow() }

    private fun convertJsonAndUpsert() = viewModelScope.launch(IO) {
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
        _currentData.emit(WeatherState.LOADING())
        repository.getCurrentData(city, API_KEY).catch {
            _currentData.emit(WeatherState.ERROR(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _currentData.emit(WeatherState.SUCCESS(it))
            }
        }
    }


    fun handleHourly(city: String) = viewModelScope.launch(IO) {
        _hourlyData.emit(WeatherState.LOADING())
        repository.getHourlyData(city, API_KEY).catch {
            _hourlyData.emit(WeatherState.ERROR(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _hourlyData.emit(WeatherState.SUCCESS(it))
            }
        }
    }

    fun handleDaily(city: String) = viewModelScope.launch(IO) {
        _dailyData.emit(WeatherState.LOADING())
        repository.getDailyData(city, API_KEY).catch {
            _dailyData.emit(WeatherState.ERROR(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _dailyData.emit(WeatherState.SUCCESS(it))
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
        _favoriteCities.emit(WeatherState.LOADING())
        repository.getAllCityFavorite().catch {
            _favoriteCities.emit(WeatherState.ERROR(it.message))
        }.collectLatest {
            _favoriteCities.emit(WeatherState.SUCCESS(it!!))
        }
    }


     fun searchCity(searchQuery:String) = viewModelScope.launch {
        _searchList.emit(WeatherState.LOADING())
        repository.searchCity(searchQuery).catch {
            _searchList.emit(WeatherState.ERROR(it.message))
        }.collectLatest {
            _searchList.emit(WeatherState.SUCCESS(it!!))
        }
    }


}