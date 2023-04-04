package com.example.weatherforecastapp.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.HourForecast
import kotlin.math.roundToInt

class TodayWeatherAdapter (private val mWeathers: List<HourForecast>) : RecyclerView.Adapter<TodayWeatherAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val timeTextView = itemView.findViewById<TextView>(R.id.time)
        val weatherIcon = itemView.findViewById<ImageView>(R.id.icon)
        val maxTemp = itemView.findViewById<TextView>(R.id.temp)
    }

    // ... constructor and member variables
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val weatherView = inflater.inflate(R.layout.recycler_item, parent, false)
        // Return a new holder instance
        return ViewHolder(weatherView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val weather: HourForecast = mWeathers.get(position)
        // Set item views based on your views and data model
        val textViewTime = viewHolder.timeTextView
        textViewTime.setText(weather.time_in_day.split(" ").get(1))
        val icon = viewHolder.weatherIcon
        Log.d("ICON", weather.hour_condition.condition_icon)
        icon.load("https:" + weather.hour_condition.condition_icon)
        val textViewTemp = viewHolder.maxTemp
        textViewTemp.text = buildString {
        append(weather.hour_temp_cel.roundToInt().toString()).append("°")
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mWeathers.size
    }
}