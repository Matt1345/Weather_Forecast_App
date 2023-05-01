package com.example.weatherforecastapp.ui.settings


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sharedPrefs = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val metricSystem = sharedPrefs?.getString("metric_system", "metric") ?: "metric"
        val currentLanguage = sharedPrefs?.getString("language", "hr") ?: "hr"

//        Log.d("Metric system", metricSystem)
//        Log.d("Language", currentLanguage)


        if(metricSystem.equals("metric")){
            binding.metricButton.isClickable = false
            binding.metricButton.alpha = 0.5f
            binding.imperialButton.isClickable = true
            binding.imperialButton.alpha = 1.0f
        }

        if(metricSystem.equals("imperial")){
            binding.imperialButton.isClickable = false
            binding.imperialButton.alpha = 0.5f
            binding.metricButton.isClickable = true
            binding.metricButton.alpha = 1.0f

        }

        if(currentLanguage.equals("hr")) {
            binding.croatianButton.isClickable = false
            binding.croatianButton.alpha = 0.5f
            binding.englishButton.isClickable = true
            binding.englishButton.alpha = 1.0f
        }

        if(currentLanguage.equals("en")) {
            binding.englishButton.isClickable = false
            binding.englishButton.alpha = 0.5f
            binding.croatianButton.isClickable = true
            binding.croatianButton.alpha = 1.0f
        }

        binding.metricButton.setOnClickListener {
            // Save the selected metric system to SharedPreferences
            //val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE)
            val sharedPrefs = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.putString("metric_system", "metric")?.apply()

            activity?.apply {
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }

        binding.imperialButton.setOnClickListener {
            val sharedPrefs = context?.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.putString("metric_system", "imperial")?.apply()

            activity?.apply {
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }

        binding.englishButton.setOnClickListener{
            val languageCode = "en"
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            val context = requireContext().createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)

            val sharedPrefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.putString("language", languageCode)?.apply()

            activity?.apply {
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

        }

        binding.croatianButton.setOnClickListener{

            val languageCode = "hr"
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            val context = requireContext().createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)

            val sharedPrefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.putString("language", languageCode)?.apply()

            activity?.apply {
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}