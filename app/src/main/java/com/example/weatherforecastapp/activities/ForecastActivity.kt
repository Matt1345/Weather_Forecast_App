package com.example.weatherforecastapp.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherforecastapp.MainViewModel
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.factories.ViewModelFactory
import com.example.weatherforecastapp.adapters.TodayWeatherAdapter
import com.example.weatherforecastapp.adapters.WeekWeatherAdapter
import com.example.weatherforecastapp.databinding.ActivityForecastBinding
import com.example.weatherforecastapp.network.ApiHelper
import com.example.weatherforecastapp.network.RetrofitBuilder
import com.example.weatherforecastapp.utils.Status.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


class ForecastActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityForecastBinding
    var isFavorite = false
    private lateinit var city : String

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val cityName = getIntent().extras?.getString("city")

        if (cityName != null) {
            city = cityName
        }

        setupViewModel()
        setupObservers()

        binding.backIcon.setOnClickListener{
            finish()
        }

        val favIcon = binding.favoriteIcon

        favIcon.setOnClickListener {
            if (isFavorite) {
                favIcon.setImageResource(R.drawable.baseline_star_border_24)
            } else {
                favIcon.setImageResource(R.drawable.full_star)
                Toast.makeText(this, "City added to favorites", Toast.LENGTH_SHORT).show()
            }
            isFavorite = !isFavorite
        }

        viewModel.forecastComplete.observe(this, Observer {
            val data = it

            val dateAndTime = formatDateAndTime(data.Location.localtime)

            binding.cityName.text = data.Location.city_name
            binding.date.text = dateAndTime[0]
            binding.time.text = dateAndTime[1]
            binding.state.text = data.Current.current_condition.condition_text

            binding.currentTemp.text = buildString { append(data.Current.current_temp_cel.roundToInt().toString()).
            append("°") }

            binding.minMaxTemp.text = buildString { append(data.forecast.week_forecast.get(0).day.min_temp_cel.roundToInt().toString())
            append("° / ").append(data.forecast.week_forecast.get(0).day.max_temp_cel.roundToInt().toString()).append("°") }

            binding.wind.text = buildString { append(data.Current.wind_km.roundToInt().toString()).append(" km/h (NW)") }
            binding.currentIcon.load("https:" + data.Current.current_condition.condition_icon)
            binding.humidity.text = buildString { append(data.Current.humidity).append(" %") }

            binding.pressure.text = buildString { append(data.Current.pressure.roundToInt().toString()).append(" hPa") }

            binding.visibility.text = buildString { append(data.Current.visibility_km.roundToInt().toString()).append(" km") }


            //DAY WEATHER
            val rvTodayWeathers = findViewById<View>(R.id.rvTodayWeathers) as RecyclerView
            val todayWeathers = it.forecast.week_forecast[0].hour_forecast
            val adapter1 = TodayWeatherAdapter(todayWeathers)
            rvTodayWeathers.adapter = adapter1
            rvTodayWeathers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val dividerItemDecoration = DividerItemDecoration(
                rvTodayWeathers.context,
                LinearLayoutManager.HORIZONTAL
            )
            rvTodayWeathers.addItemDecoration(dividerItemDecoration)

            //WEEK WEATHER
            val rvWeekWeathers = findViewById<View>(R.id.rvWeekWeathers) as RecyclerView
            val weekWeathers = it.forecast.week_forecast
            //Ovo je da maknemo danasnji dan
            weekWeathers.removeFirst()
            val adapter2 = WeekWeatherAdapter(weekWeathers)
            // Attach the adapter to the recyclerview to populate items
            rvWeekWeathers.adapter = adapter2
            // Set layout manager to position the items
            rvWeekWeathers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            // That's all!
        })

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService, city = city))
        ).get(MainViewModel::class.java)
    }


    private fun setupObservers() {
        viewModel.getForecast().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        resource.data?.let { forecast ->
                            forecast.body()?.let { it1 -> viewModel.addForecast(it1) }
                        }
                        Toast.makeText(this, "Great succes", Toast.LENGTH_LONG).show()
//                      Log.d("Logbinator: ", viewModel.forecastComplete.value.toString())
//                      Log.d("Logbinatorcina", forecastComplete.value?.forecast?.week_forecast.toString())
                    }
                    ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    LOADING -> {
                        Toast.makeText(this, "Forecast is loading", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)

    fun formatDateAndTime(dateTimeString: String): Array<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd[ H:mm][ h:mm a]", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)

        val dateString = dateTime.format(DateTimeFormatter.ofPattern("EEE, MMMM d", Locale.ENGLISH))
            .replaceFirstChar { it.uppercase() }
        val timeString = dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

        return arrayOf(dateString, timeString)
    }


}