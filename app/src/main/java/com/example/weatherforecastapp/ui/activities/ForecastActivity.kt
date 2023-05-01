package com.example.weatherforecastapp.ui.activities

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherforecastapp.database.DatabaseBuilder
import com.example.weatherforecastapp.database.DatabaseHelperImpl
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.factories.ViewModelFactory
import com.example.weatherforecastapp.adapters.WeatherRecyclerAdapter
import com.example.weatherforecastapp.data.City_dbclass
import com.example.weatherforecastapp.databinding.ActivityForecastBinding
import com.example.weatherforecastapp.network.ApiHelper
import com.example.weatherforecastapp.network.RetrofitBuilder
import com.example.weatherforecastapp.utils.Status.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


class ForecastActivity : AppCompatActivity(), OnMapReadyCallback {

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

        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val metricSystem = sharedPrefs?.getString("metric_system", "metric") ?: "metric"
        val currentLanguage = sharedPrefs?.getString("language", "hr") ?: "hr"

        Log.d("Metric system", metricSystem)
        Log.d("Language", currentLanguage)

        if (cityName != null) {
            city = cityName
        }

        setupViewModel()
        setupObservers()

        binding.backIcon.setOnClickListener{
            finish()
        }

        val favIcon = binding.favoriteIcon

        val citiesDatabase = DatabaseBuilder.getInstance(this.applicationContext)
        val dbHelper = DatabaseHelperImpl(citiesDatabase)

        lifecycleScope.launch {
            val cities = dbHelper.getCities()
            val city_new = City_dbclass(cityName = city)
            for(cite in cities) {
                if(city_new.cityName.equals(cite.cityName)){
                    isFavorite = true
                    favIcon.setImageResource(R.drawable.full_star)
                }
            }}


        favIcon.setOnClickListener {
            val city = City_dbclass(cityName = binding.cityName.text as String)
            if (isFavorite) {
                favIcon.setImageResource(R.drawable.baseline_star_border_24)
                lifecycleScope.launch {
                    val cities = dbHelper.getCities()
                    for(city_new in cities) {
                        if(city_new.equals(city)){
                            dbHelper.removeCity(city_new)
                            isFavorite = false
                        }
                    }
                    val cities_new = dbHelper.getCities()
                    Log.d("gradovi_novi", cities_new.toString())
                }
                var toast_text = "City moved from favorites"
                if(currentLanguage.equals("hr")) toast_text = "Grad maknut iz favorita"
                Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show()
            } else {
                favIcon.setImageResource(R.drawable.full_star)
                var toast_text = "City added to favorites"
                if(currentLanguage.equals("hr")) toast_text = "Grad dodan u favorite"
                Toast.makeText(this, toast_text, Toast.LENGTH_SHORT).show()

                lifecycleScope.launch {
                    dbHelper.insertCity(city)
                    val cities = dbHelper.getCities()
                    Log.d("gradovi", cities.toString())
                }
                isFavorite = true
            }

        }

        viewModel.forecastComplete.observe(this, Observer {
            val data = it

            val dateAndTime = formatDateAndTime(data.Location.localtime)

            binding.cityName.text = data.Location.city_name
            binding.currentIcon.load("https:" + data.Current.current_condition.condition_icon)
            binding.date.text = dateAndTime[0]
            binding.time.text = dateAndTime[1]
            binding.state.text = data.Current.current_condition.condition_text

            if(metricSystem.equals("metric")) {
                binding.currentTemp.text =
                    getString(R.string.current_temp_cel, data.Current.current_temp_cel.roundToInt())
                binding.minMaxTemp.text = getString(
                    R.string.min_max_temp_cel,
                    data.forecast.week_forecast.get(0).day.min_temp_cel.roundToInt(),
                    data.forecast.week_forecast.get(0).day.max_temp_cel.roundToInt()
                )
                binding.visibility.text =
                    getString(R.string.visibility_km, data.Current.visibility_km.roundToInt())
                binding.wind.text = getString(R.string.wind_kmh, data.Current.wind_km.roundToInt())
                binding.visibility.text =
                    getString(R.string.visibility_km, data.Current.visibility_km.roundToInt())
            }

            else{
                binding.currentTemp.text =
                    getString(R.string.current_temp_fahr, data.Current.current_temp_fahr.roundToInt())
                binding.minMaxTemp.text = getString(
                    R.string.min_max_temp_fahr,
                    data.forecast.week_forecast.get(0).day.min_temp_fahr.roundToInt(),
                    data.forecast.week_forecast.get(0).day.max_temp_fahr.roundToInt()
                )
                binding.visibility.text =
                    getString(R.string.visibility_mil, data.Current.visibility_miles.roundToInt())
                binding.wind.text = getString(R.string.wind_mph, data.Current.wind_miles.roundToInt())
                binding.visibility.text =
                    getString(R.string.visibility_mil, data.Current.visibility_miles.roundToInt())
            }


            binding.humidity.text = getString(R.string.humidity, data.Current.humidity)
            binding.pressure.text = getString(R.string.pressure, data.Current.pressure.roundToInt())



            //DAY WEATHER
            val todayView = binding.todayWeatherView
            todayView.findViewById<TextView>(R.id.custom_title).text = getString(R.string.today)
            val rvTodayWeathers = todayView.findViewById<RecyclerView>(R.id.recyclerView)
            val todayWeathers = it.forecast.week_forecast[0].hour_forecast
            val adapter1 = WeatherRecyclerAdapter(this, todayWeathers)
            rvTodayWeathers.adapter = adapter1
            rvTodayWeathers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val dividerItemDecoration = DividerItemDecoration(
                rvTodayWeathers.context,
                LinearLayoutManager.HORIZONTAL
            )
            rvTodayWeathers.addItemDecoration(dividerItemDecoration)

            //WEEK WEATHER
            val weekView = binding.weekWeatherView
            weekView.findViewById<TextView>(R.id.custom_title).text = getString(R.string.next_seven_days)
            val rvWeekWeathers = weekView.findViewById<RecyclerView>(R.id.recyclerView)
            val weekWeathers = it.forecast.week_forecast
            //Ovo je da maknemo danasnji dan
            weekWeathers.removeFirst()
            val adapter2 = WeatherRecyclerAdapter(this,weekWeathers)
            // Attach the adapter to the recyclerview to populate items
            rvWeekWeathers.adapter = adapter2
            // Set layout manager to position the items
            rvWeekWeathers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            // That's all!
            rvWeekWeathers.addItemDecoration(dividerItemDecoration)


            val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?


            mapFragment?.getMapAsync{googleMap ->
                val cityName = data.Location.city_name


                val geocoder = Geocoder(this, Locale.getDefault())
                var lat: Double = 1.0
                var long: Double = 1.0

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(cityName,1,
                        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                        object : Geocoder.GeocodeListener{
                            override fun onGeocode(addresses: MutableList<Address>) {
                                Log.d("GeocodeListener", "onGeocode: addresses size = ${addresses.size}")
                                if (addresses.isNotEmpty()) {
                                    val result = addresses[0]
                                    Log.d("GeocodeListener", "onGeocode: result = $result")
                                    lat = result.latitude
                                    long = result.longitude
                                    Log.d("GeocodeListener", "onGeocode: lat = $lat, long = $long")
                                }
                            }
                        override fun onError(errorMessage: String?) {
                            super.onError(errorMessage)
                        }

                    })
                }
                val latLng = LatLng(lat , long)
                googleMap.addMarker(MarkerOptions().position(latLng).title(cityName))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }
        })

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService, city = city))
        ).get(MainViewModel::class.java)
    }


    private fun setupObservers() {
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val currentLanguage = sharedPrefs?.getString("language", "hr") ?: "hr"
        viewModel.getForecast().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        resource.data?.let { forecast ->
                            forecast.body()?.let { forecast_data -> viewModel.addForecast(forecast_data) }
                        }
                        if(currentLanguage.equals("en")) {
                            Toast.makeText(this, "Forecast loaded", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Prognoza se učitala", Toast.LENGTH_SHORT).show()
                        }

                    }
                    ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    LOADING -> {
                        if(currentLanguage.equals("en")) {
                            Toast.makeText(this, "Forecast is loading", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Prognoza se učitava", Toast.LENGTH_SHORT).show()
                        }
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

    override fun onMapReady(p0: GoogleMap) {

    }


}