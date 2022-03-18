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

    private val _currentData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val currentData: StateFlow<WeatherState> = _currentData

    private val _hourlyData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val hourlyData: StateFlow<WeatherState> = _hourlyData

    private val _dailyData = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val dailyData: StateFlow<WeatherState> = _dailyData

    private val _allCities = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val allCities: StateFlow<WeatherState> = _allCities

    private val _favoriteCities = MutableStateFlow<WeatherState>(WeatherState.Empty)
    val favoriteCities: StateFlow<WeatherState> = _favoriteCities

    val searchQuery = MutableStateFlow("")
/*    val searchFavoriteQuery = MutableStateFlow("")*/

    init {

    }

    fun convertJsonAndUpsert() {
        job = viewModelScope.launch(Default) {
            async { json() }.await()
            async { repository.upserts(HiltApplication.cities) }.await()
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

    fun handleCurrentData(city: String) {
        job = viewModelScope.launch(IO) {
            val current = async {

                _currentData.value = WeatherState.Loading
                repository.getCurrentData(city, API_KEY).catch {
                    _currentData.value = WeatherState.Error(it.message)
                }.collectLatest {
                    if (it.isSuccessful) {
                        _currentData.value = WeatherState.SuccessCurrent(it)
                    }
                }
            }
            current.await()
        }
    }

    fun handleHourly(city: String) {
        job = viewModelScope.launch(IO) {
            val hourly = async {
                _hourlyData.value = WeatherState.Loading
                repository.getHourlyData(city, API_KEY).catch {
                    _hourlyData.value = WeatherState.Error(it.message)
                }.collectLatest {
                    if (it.isSuccessful) {
                        _hourlyData.value = WeatherState.SuccessHourly(it)
                    }
                }
            }
            hourly.await()
        }
    }

    fun handleDaily(city: String) {
        job = viewModelScope.launch(IO) {
            val daily = async {
                _dailyData.value = WeatherState.Loading
                repository.getDailyData(city, API_KEY).catch {
                    _dailyData.value = WeatherState.Error(it.message)
                }.collectLatest {
                    if (it.isSuccessful) {
                        _dailyData.value = WeatherState.SuccessDaily(it)
                    }
                }
            }
            daily.await()
        }

    }


    fun addFavoriteCity(favorite: Favorite) {
        job = viewModelScope.launch {
            val add = async { repository.favoriteUpsert(favorite) }
            add.await()
        }
    }

    fun deleteFavoriteCity(favorite: Favorite) {
        job = viewModelScope.launch {
            val delete = async { repository.deleteFavorite(favorite) }
            delete.await()
        }
    }


    fun handleAllCity() {
        job = viewModelScope.launch(IO) {
            val allCity = async {
                _allCities.value = WeatherState.Loading
                repository.getCities().catch {
                    _allCities.value = WeatherState.Error(it.message)
                }.collectLatest {
                    _allCities.value = WeatherState.SearchCity(it!!)

                }
            }
            allCity.await()
        }
    }


    fun handleAllFavoriteCity() {
        job = viewModelScope.launch(IO) {
            val favorite = async {
                _favoriteCities.value = WeatherState.Loading
                repository.getAllCityFavorite().catch {
                    _favoriteCities.value = WeatherState.Error(it.message)
                }.collectLatest {
                    _favoriteCities.value = WeatherState.SearchFavoriteCity(it!!)
                }
            }
            favorite.await()
        }
    }


    @ExperimentalCoroutinesApi
    private fun searchCity() = searchQuery.flatMapLatest {
        repository.searchCity(it)
    }


 /*   @ExperimentalCoroutinesApi
    private fun searchFavoriteCity() = searchFavoriteQuery.flatMapLatest { repository.searchFavoriteCity(it) }
*/

    @ExperimentalCoroutinesApi
    fun searchCities() = searchCity()

/*    @ExperimentalCoroutinesApi
    fun searchFavoriteCities() = searchFavoriteCity()*/

    override fun onCleared() {
        super.onCleared()
        job!!.cancel()
        job = null
    }

}