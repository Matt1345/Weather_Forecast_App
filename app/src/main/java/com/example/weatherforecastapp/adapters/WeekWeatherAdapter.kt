package com.example.weatherforecastapp.adapters

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
import kotlin.math.roundToInt
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeekWeatherAdapter (private val mWeathers: List<DateForecast>) : RecyclerView.Adapter<WeekWeatherAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val dayTextView = itemView.findViewById<TextView>(R.id.day)
        val weatherIcon = itemView.findViewById<ImageView>(R.id.icon2)
        val avgDayTemp = itemView.findViewById<TextView>(R.id.temp2)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val weatherView = inflater.inflate(R.layout.recycler_item_2, parent, false)
        // Return a new holder instance
        return ViewHolder(weatherView)
    }

    // Involves populating data into the item through holder
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val weather: DateForecast = mWeathers.get(position)
        // Set item views based on your views and data model
        val textViewDay = viewHolder.dayTextView
        textViewDay.text = getDayOfWeek(weather.date)
        val icon = viewHolder.weatherIcon
        icon.load("https:" + weather.day.dayCondition.condition_icon)
        val textViewTemp = viewHolder.avgDayTemp
        textViewTemp.text = buildString {
            append(weather.day.avg_temp_cel.roundToInt().toString()).append("°")
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
}