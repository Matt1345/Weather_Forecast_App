package com.example.weatherforecastapp.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.DateForecast
import com.example.weatherforecastapp.data.HourForecast
import kotlin.math.roundToInt
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//Enum of supported objects in recycler adapter
enum class ItemType {
    HOUR_FORECAST,
    DATE_FORECAST
}

class WeatherRecyclerAdapter(private val context: Context, private val mWeathers: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class HourForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView = itemView.findViewById<TextView>(R.id.day_or_time)
        val weatherIcon = itemView.findViewById<ImageView>(R.id.icon)
        val maxTempTextView = itemView.findViewById<TextView>(R.id.temp)
    }

    inner class DateForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView = itemView.findViewById<TextView>(R.id.day_or_time)
        val weatherIcon = itemView.findViewById<ImageView>(R.id.icon)
        val avgDayTempTextView = itemView.findViewById<TextView>(R.id.temp)
    }

    override fun getItemViewType(position: Int): Int {
        return when (mWeathers[position]) {
            is HourForecast -> ItemType.HOUR_FORECAST.ordinal
            is DateForecast -> ItemType.DATE_FORECAST.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }
    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.HOUR_FORECAST -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
                HourForecastViewHolder(itemView)
            }
            ItemType.DATE_FORECAST -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
                DateForecastViewHolder(itemView)
            }
        }
    }

    // Involves populating data into the item through holder
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sharedPrefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val metricSystem = sharedPrefs?.getString("metric_system", "metric") ?: "metric"
        val currentLanguage = sharedPrefs?.getString("language", "hr") ?: "hr"

        when (holder) {
            is HourForecastViewHolder -> {
                val hourForecast = mWeathers[position] as HourForecast
                holder.timeTextView.text = hourForecast.time_in_day.split(" ").get(1)
                holder.weatherIcon.load("https:" + hourForecast.hour_condition.condition_icon)
                if(metricSystem.equals("metric")) {
                    holder.maxTempTextView.text = "${hourForecast.hour_temp_cel.roundToInt()}°"
                }
                else{
                    holder.maxTempTextView.text = "${hourForecast.hour_temp_fahr.roundToInt()}°"
                }
            }
            is DateForecastViewHolder -> {
                val dateForecast = mWeathers[position] as DateForecast
                if(currentLanguage.equals("en")) {
                    holder.dayTextView.text = getDayOfWeek(dateForecast.date)
                }
                else{
                    holder.dayTextView.text = getDanUTjednu(dateForecast.date)
                }
                holder.weatherIcon.load("https:" + dateForecast.day.dayCondition.condition_icon)
                if(metricSystem.equals("metric")){
                    holder.avgDayTempTextView.text = "${dateForecast.day.avg_temp_cel.roundToInt()}°"
                }
                else{
                    holder.avgDayTempTextView.text = "${dateForecast.day.avg_temp_faht.roundToInt()}°"
                }
            }
            else -> throw IllegalArgumentException("Unknown view holder type")
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mWeathers.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeek(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek.toString().substring(0, 3)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDanUTjednu(datumString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datum = LocalDate.parse(datumString, formatter)
        val danUTjednu = datum.dayOfWeek
        val dani = arrayOf("PON", "UTO", "SRI", "ČET", "PET", "SUB", "NED")
        return dani[danUTjednu.value - 1]
    }
}