package com.example.alertdialogdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class WeatherBriefFragment : Fragment() {
    
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView

    private val weatherReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val city = intent?.getStringExtra("city")
            val temp = intent?.getDoubleExtra("temp", 0.0)
            val currentFragmentCity = arguments?.getString(ARG_CITY)

            if (city?.equals(currentFragmentCity, ignoreCase = true) == true) {
                tvCity.text = city
                tvTemp.text = "${temp}°C"
            }
        }
    }

    companion object {
        private const val ARG_CITY = "city"
        fun newInstance(city: String) = WeatherBriefFragment().apply {
            arguments = Bundle().apply { putString(ARG_CITY, city) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_brief, container, false)
        tvCity = view.findViewById(R.id.tv_city)
        tvTemp = view.findViewById(R.id.tv_temp)
        
        val city = arguments?.getString(ARG_CITY) ?: "Moscow"
        tvCity.text = city
        
        // Загружаем сохраненную температуру
        val prefs = requireContext().getSharedPreferences(WeatherWorker.PREFS_NAME, Context.MODE_PRIVATE)
        val savedTemp = prefs.getFloat("${city}_temp", 25.0f)
        tvTemp.text = "${savedTemp}°C"
        
        return view
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(WeatherWorker.ACTION_WEATHER_UPDATE)
        ContextCompat.registerReceiver(requireContext(), weatherReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(weatherReceiver)
    }
}