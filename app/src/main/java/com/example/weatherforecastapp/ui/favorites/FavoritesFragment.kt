package com.example.weatherforecastapp.ui.favorites

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapp.*
import com.example.weatherforecastapp.ui.activities.ForecastActivity
import com.example.weatherforecastapp.data.City_dbclass
import com.example.weatherforecastapp.database.DatabaseBuilder
import com.example.weatherforecastapp.database.DatabaseHelper
import com.example.weatherforecastapp.database.DatabaseHelperImpl
import com.example.weatherforecastapp.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var cityRecyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var cityList: List<City_dbclass>
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        cityRecyclerView = binding.cityList
        cityRecyclerView.layoutManager = LinearLayoutManager(context)

        databaseHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))

        val space = resources.getDimensionPixelSize(R.dimen.list_item_space)
        cityRecyclerView.addItemDecoration(SpaceItemDecoration(space))


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCities()

    }

    private fun loadCities() {
        CoroutineScope(Dispatchers.Main).launch {
            cityList = databaseHelper.getCities()
            cityAdapter = CityAdapter(cityList)
            cityRecyclerView.adapter = cityAdapter
            val callback = ItemTouchHelperCallback(cityAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(cityRecyclerView)
        }
    }

    private inner class CityHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var city: City_dbclass

        private val cityNameTextView: TextView = itemView.findViewById(R.id.city_name)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(city: City_dbclass) {
            this.city = city
            cityNameTextView.text = city.cityName
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedCity = cityList[position]

                val sharedPrefs = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                val currentLanguage = sharedPrefs?.getString("language", "hr") ?: "hr"

                val builder = AlertDialog.Builder(context)
                var confirm = "Confirm"
                var message = "Are you sure you want to check the forecast for ${clickedCity.cityName}?"
                var yesButton = "Yes"
                var noButton = "No"
                if(currentLanguage.equals("hr")) {
                    confirm = "Potvrdi"
                    message = "Jeste li sigurni da želite vidjeti prognozu za ${clickedCity.cityName}?"
                    yesButton = "Da"
                    noButton = "Ne"
                }
                builder.setTitle(confirm)

                builder.setMessage(message)
                builder.setPositiveButton(yesButton) { _, _ ->
                    val intent = Intent(context, ForecastActivity::class.java)
                    intent.putExtra("city", clickedCity.cityName)
                    startActivity(intent)
                }
                builder.setNegativeButton(noButton) { _, _ -> }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private inner class CityAdapter(private val cityList: List<City_dbclass>) : RecyclerView.Adapter<CityHolder>(),
        ItemTouchHelperCallback.ItemTouchHelperAdapter {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
            val view = layoutInflater.inflate(R.layout.list_item_city, parent, false)
            return CityHolder(view)
        }

        override fun onBindViewHolder(holder: CityHolder, position: Int) {
            val city = cityList[position]
            holder.bind(city)
        }

        override fun getItemCount(): Int = cityList.size
        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(cityList, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(cityList, i, i - 1)
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                val oldCityList = databaseHelper.getCities()
                for(city in oldCityList) databaseHelper.removeCity(city)
                for(city in cityList) databaseHelper.insertCity(City_dbclass(cityName = city.cityName))
                val newCityList = databaseHelper.getCities()

            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemDismiss(position: Int) {
            notifyItemRemoved(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}