package com.example.weatherforecastapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.factories.ViewModelFactoryAutofill
import com.example.weatherforecastapp.activities.ForecastActivity
import com.example.weatherforecastapp.data.City
import com.example.weatherforecastapp.databinding.FragmentSearchBinding
import com.example.weatherforecastapp.network.ApiHelperAutofill
import com.example.weatherforecastapp.network.RetrofitBuilder
import com.example.weatherforecastapp.utils.Status

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupViewModel()
        setupObservers()


        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchIcon.setOnClickListener{
            val intent = Intent(this.context, ForecastActivity::class.java)
            intent.putExtra("city", binding.citySearch.text.toString())
            startActivity(intent)
        }

        viewModel.autocompleteFiller.observe(viewLifecycleOwner, Observer<ArrayList<City>> {
            val arrayAdapter = ArrayAdapter(this.requireContext(), android.R.layout.simple_list_item_1, it)
            binding.citySearch.setAdapter(arrayAdapter)
        })

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactoryAutofill(ApiHelperAutofill(RetrofitBuilder.apiService, filler_name = "Lond"))
        ).get(SearchViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getAutocomplete().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { cities -> viewModel.addAutofill(cities)
                            Toast.makeText(this.context, "We got recommendations.", Toast.LENGTH_SHORT).show()
                        }
//                      Log.d("Logbinator: ", viewModel.forecastComplete.value.toString())
//                      Log.d("Logbinatorcina", forecastComplete.value?.forecast?.week_forecast.toString())
                    }
                    Status.ERROR -> {
                        Log.d("LOGBAAA", resource.data.toString())
                        Toast.makeText(this.context, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        Toast.makeText(this.context, "Forecast is loading", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}