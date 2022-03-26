package com.example.globalweather.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private lateinit var cities: MutableList<City>

    private val _currentData = MutableLiveData<WeatherState>()
    val currentData: LiveData<WeatherState> = _currentData

    private val _hourlyData = MutableLiveData<WeatherState>()
    val hourlyData: LiveData<WeatherState> = _hourlyData

    private val _dailyData = MutableLiveData<WeatherState>()
    val dailyData: LiveData<WeatherState> = _dailyData

    private val _allCities = MutableLiveData<WeatherState>()
    val allCities: LiveData<WeatherState> = _allCities

    private val _favoriteCities = MutableLiveData<WeatherState>()
    val favoriteCities: LiveData<WeatherState> = _favoriteCities

    val searchQuery = MutableStateFlow("")


    fun convertJsonAndUpsert() = viewModelScope.launch(IO) {
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

    fun handleCurrentData(city: String) = viewModelScope.launch {
        _currentData.postValue(WeatherState.Loading)
        repository.getCurrentData(city, API_KEY).catch {
            _currentData.postValue(WeatherState.Error(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _currentData.postValue(WeatherState.SuccessCurrent(it))
            }
        }
    }


    fun handleHourly(city: String) = viewModelScope.launch {
        _hourlyData.postValue(WeatherState.Loading)
        repository.getHourlyData(city, API_KEY).catch {
            _hourlyData.postValue(WeatherState.Error(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _hourlyData.postValue(WeatherState.SuccessHourly(it))
            }
        }
    }

    fun handleDaily(city: String) = viewModelScope.launch {
        _dailyData.postValue(WeatherState.Loading)
        repository.getDailyData(city, API_KEY).catch {
            _dailyData.postValue(WeatherState.Error(it.message))
        }.collectLatest {
            if (it.isSuccessful) {
                _dailyData.postValue(WeatherState.SuccessDaily(it))
            }
        }
    }


    fun addFavoriteCity(favorite: Favorite) = viewModelScope.launch {
        repository.favoriteUpsert(favorite)
    }


    fun deleteFavoriteCity(favorite: Favorite) = viewModelScope.launch {
        repository.deleteFavorite(favorite)
    }


    fun handleAllCity() = viewModelScope.launch {
        _allCities.postValue(WeatherState.Loading)
        repository.getCities().catch {
            _allCities.postValue(WeatherState.Error(it.message))
        }.collectLatest {
            _allCities.postValue(WeatherState.SearchCity(it!!))
        }
    }


    fun handleAllFavoriteCity() = viewModelScope.launch {
        _favoriteCities.postValue(WeatherState.Loading)
        repository.getAllCityFavorite().catch {
            _favoriteCities.postValue(WeatherState.Error(it.message))
        }.collectLatest {
            _favoriteCities.postValue(WeatherState.SearchFavoriteCity(it!!))
        }
    }


    @ExperimentalCoroutinesApi
    private fun searchCity() = searchQuery.flatMapLatest {
        repository.searchCity(it)
    }

    @ExperimentalCoroutinesApi
    fun searchCities() = searchCity()



}