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
import com.example.globalweather.utils.WeatherState
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.Charsets.UTF_8

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private var job: Job? = null
    private lateinit var cities: MutableList<City>

    private val currentData: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.Empty)
    private val hourlyData: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.Empty)
    private val dailyData: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.Empty)
    private val allCities: MutableStateFlow<WeatherState> = MutableStateFlow(WeatherState.Empty)
    private val favoriteCities: MutableStateFlow<WeatherState> =
        MutableStateFlow(WeatherState.Empty)

    val searchQuery = MutableStateFlow("")
    val searchFavoriteQuery = MutableStateFlow("")

    init {

    }

    fun convertJsonAndUpsert() {
        job = viewModelScope.launch(Default) {
            async { json() }.await()
            async { repository.upserts(HiltApplication.cities) }.await()
        }

    }


    fun addFavoriteCity(favorite: Favorite) {
        job = viewModelScope.launch {
            val add = async { repository.fInsert(favorite) }
            add.await()
        }
    }

    fun deleteFavoriteCity(favorite: Favorite){
        job = viewModelScope.launch {
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

    private suspend fun handleCurrent(city: String) {
        currentData.value = WeatherState.Loading
        repository.getCurrentData(city, API_KEY).catch { e ->
            currentData.value = WeatherState.Error(e)
        }.collectLatest {
            if (it.isSuccessful) {
                currentData.value = WeatherState.SuccessCurrent(it)
            }

        }
    }

    fun current(city: String) {
        job = viewModelScope.launch(IO)  {
            val current = async{ handleCurrent(city) }
            current.await()
        }
    }

    private suspend fun handleHourly(city: String) {
        hourlyData.value = WeatherState.Loading
        repository.getHourlyData(city, API_KEY).catch { e ->
            hourlyData.value = WeatherState.Error(e)
        }.collectLatest {
            if (it.isSuccessful) {
                hourlyData.value = WeatherState.SuccessHourly(it)
            }
        }
    }

    fun hourly(city: String) {
        job = viewModelScope.launch(IO) {
            val hourly = async { handleHourly(city) }
            hourly.await()
        }
    }

    private suspend fun handleDaily(city: String) {
        dailyData.value = WeatherState.Loading
        repository.getDailyData(city, API_KEY).catch { e ->
            dailyData.value = WeatherState.Error(e)
        }.collectLatest {
            if (it.isSuccessful) {
                dailyData.value = WeatherState.SuccessDaily(it)
            }
        }
    }

    fun daily(city: String) {
        job = viewModelScope.launch(IO) {
            val daily = async { handleDaily(city) }
            daily.await()
        }
    }

    private suspend fun handleAllCity() {
        allCities.value = WeatherState.Loading
        repository.getCities().catch { e ->
            allCities.value = WeatherState.Error(e)
        }.collectLatest {
            allCities.value = WeatherState.SearchCity(it!!)
        }
    }

    fun cities() {
        job = viewModelScope.launch(IO) {
            val cities = async { handleAllCity() }
            cities.await()
        }
    }

    private suspend fun handleAllFavoriteCity() {
        favoriteCities.value = WeatherState.Loading
        repository.getAllCityFavorite().catch { e ->
            favoriteCities.value = WeatherState.Error(e)
        }.collectLatest {
            favoriteCities.value = WeatherState.SearchFavoriteCity(it!!)
        }
    }

    fun favoriteCities() {
        job = viewModelScope.launch(IO) {
            val favoriteCities = async { handleAllFavoriteCity() }
            favoriteCities.await()
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


    fun getCurrent(): StateFlow<WeatherState> = currentData

    fun getHourly(): StateFlow<WeatherState> = hourlyData

    fun getDaily(): StateFlow<WeatherState> = dailyData

    fun getAllCity(): StateFlow<WeatherState> = allCities

    fun getAllFavoriteCity(): StateFlow<WeatherState> = favoriteCities

    override fun onCleared() {
        super.onCleared()
        job!!.cancel()
        job = null
    }

}