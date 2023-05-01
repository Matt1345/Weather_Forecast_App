package com.example.weatherforecastapp.data

import com.google.gson.annotations.SerializedName

data class CompleteForecast(
    @SerializedName("location")
    val Location: Location,
    @SerializedName("current")
    val Current : Current,
    @SerializedName("forecast")
    val forecast: Forecast,
)

data class Location(
    @SerializedName("name")
    val city_name: String,
    @SerializedName("localtime")
    val localtime: String,
)

data class Current(
    @SerializedName("wind_kph")
    val wind_km: Float,
    @SerializedName("wind_mph")
    val wind_miles: Float,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure_mb")
    val pressure: Float,
    @SerializedName("vis_km")
    val visibility_km: Float,
    @SerializedName("vis_miles")
    val visibility_miles: Float,
    @SerializedName("temp_c")
    val current_temp_cel: Float,
    @SerializedName("temp_f")
    val current_temp_fahr: Float,
    @SerializedName("condition")
    val current_condition: Condition,
)

data class Forecast(
    @SerializedName("forecastday")
    val week_forecast: ArrayList<DateForecast>,
)

data class DateForecast(
    @SerializedName("date")
    val date: String,
    @SerializedName("day")
    val day: DayForecast,
    @SerializedName("hour")
    val hour_forecast: ArrayList<HourForecast>,
)

data class DayForecast(
    @SerializedName("mintemp_c")
    val min_temp_cel: Float,
    @SerializedName("mintemp_f")
    val min_temp_fahr: Float,
    @SerializedName("maxtemp_c")
    val max_temp_cel: Float,
    @SerializedName("maxtemp_f")
    val max_temp_fahr: Float,
    @SerializedName("avgtemp_c")
    val avg_temp_cel: Float,
    @SerializedName("avgtemp_f")
    val avg_temp_faht: Float,
    @SerializedName("condition")
    val dayCondition : Condition
)

data class HourForecast(
    @SerializedName("time")
    val time_in_day: String,
    @SerializedName("temp_c")
    val hour_temp_cel: Float,
    @SerializedName("temp_f")
    val hour_temp_fahr: Float,
    @SerializedName("condition")
    val hour_condition: Condition
)


data class Condition(
    @SerializedName("text")
    val condition_text: String,
    @SerializedName("icon")
    val condition_icon: String,
)
