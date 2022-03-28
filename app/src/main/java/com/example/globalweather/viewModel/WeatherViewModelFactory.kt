package com.example.globalweather.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.globalweather.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherViewModelFactory @Inject constructor(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(repository) as T
    }
}