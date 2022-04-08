package com.example.globalweather.utils



sealed class WeatherState<T>(
    val data: T? = null,
    val error: String? = null
) {

    class SUCCESS<T>(data: T) : WeatherState<T>(data)
    class LOADING<T> : WeatherState<T>()
    class ERROR<T>(throwable: String?, data: T? = null) : WeatherState<T>(data, throwable)
}




