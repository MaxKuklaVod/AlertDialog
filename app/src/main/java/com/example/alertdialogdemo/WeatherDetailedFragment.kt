package com.example.alertdialogdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class WeatherDetailedFragment : Fragment() {

    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWind: TextView

    private val weatherReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            val city = intent.getStringExtra("city")
            val currentFragmentCity = arguments?.getString(ARG_CITY)

            if (city?.equals(currentFragmentCity, ignoreCase = true) == true) {
                tvCity.text = city
                tvTemp.text = "${intent.getDoubleExtra("temp", 0.0)}°C"
                tvCondition.text = intent.getStringExtra("desc")
                tvHumidity.text = "Влажность: ${intent.getIntExtra("humidity", 0)}%"
                tvWind.text = "Ветер: ${intent.getDoubleExtra("speed", 0.0)} м/с"
            }
        }
    }

    companion object {
        private const val ARG_CITY = "city"
        fun newInstance(city: String) = WeatherDetailedFragment().apply {
            arguments = Bundle().apply { putString(ARG_CITY, city) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_detailed, container, false)
        tvCity = view.findViewById(R.id.tv_city)
        tvTemp = view.findViewById(R.id.tv_temp)
        tvCondition = view.findViewById(R.id.tv_condition)
        tvHumidity = view.findViewById(R.id.tv_humidity)
        tvWind = view.findViewById(R.id.tv_wind)

        val city = arguments?.getString(ARG_CITY) ?: "Moscow"
        tvCity.text = city

        // Загружаем сохраненные данные
        val prefs = requireContext().getSharedPreferences(WeatherWorker.PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains("${city}_temp")) {
            tvTemp.text = "${prefs.getFloat("${city}_temp", 0f)}°C"
            tvCondition.text = prefs.getString("${city}_desc", "")
            tvHumidity.text = "Влажность: ${prefs.getInt("${city}_humidity", 0)}%"
            tvWind.text = "Ветер: ${prefs.getFloat("${city}_wind", 0f)} м/с"
        }
        
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