package com.example.weatherforecastapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecastapp.database.DatabaseBuilder
import com.example.weatherforecastapp.database.DatabaseHelperImpl
import com.example.weatherforecastapp.ui.activities.ForecastActivity
import com.example.weatherforecastapp.data.City
import com.example.weatherforecastapp.data.City_dbclass
import com.example.weatherforecastapp.databinding.FragmentSearchBinding
import com.example.weatherforecastapp.factories.ViewModelFactoryAutofill
import com.example.weatherforecastapp.network.ApiHelperAutofill
import com.example.weatherforecastapp.network.RetrofitBuilder
import com.example.weatherforecastapp.utils.Status
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel

    private lateinit var viewModelProvider: ViewModelProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchIcon.setOnClickListener{
            val intent = Intent(this.context, ForecastActivity::class.java)
            intent.putExtra("city", binding.citySearch.text.toString())
            startActivity(intent)
        }

        binding.citySearch.threshold = 3

        binding.citySearch.addTextChangedListener(object : TextWatcher {
            val THRESHOLD_LENGTH = 3
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                if (searchText.length == THRESHOLD_LENGTH) {
                    // Perform an API call to get new cities.
                    resetViewModelProvider(searchText)
                    setupViewModel()
                    setupObservers()
                    setObserver()

                } else if (searchText.length < THRESHOLD_LENGTH) {
                   // binding.citySearch.setAdapter(null)

                } else {

                }
            }})

        val citiesDatabase = DatabaseBuilder.getInstance(this.requireContext())

        val city1 = City_dbclass(cityName = "New York")

        val dbHelper = DatabaseHelperImpl(citiesDatabase)

        lifecycleScope.launch {
            val cities = dbHelper.getCities()
            Log.d("gradovi_main", cities.toString())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setObserver(){
        viewModel.autocompleteFiller.observe(viewLifecycleOwner, Observer<List<City>> {
            val array = it.stream().map { city -> city.name }.toArray()
            val arrayAdapter = ArrayAdapter(this.requireContext(), android.R.layout.select_dialog_item, array)
            binding.citySearch.setAdapter(arrayAdapter)
            binding.citySearch.showDropDown()
        })
    }

//    private fun setupViewModelProvider(search : String) {
//        viewModelProvider = ViewModelProvider(
//            this,
//            ViewModelFactoryAutofill(ApiHelperAutofill(RetrofitBuilder.apiService, filler_name = search))
//        )
//    }

    private fun resetViewModelProvider(search : String) {
        viewModelProvider = ViewModelProvider(
            ViewModelStore(),
            ViewModelFactoryAutofill(ApiHelperAutofill(RetrofitBuilder.apiService, filler_name = search))
        )
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider.get(SearchViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getAutocomplete().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { cities_response ->
                            cities_response.body()?.let { cities -> viewModel.addAutofill(cities) }
                            //Toast.makeText(this.context, "We got recommendations.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this.context, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        //Toast.makeText(this.context, "Autofill is loading", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}