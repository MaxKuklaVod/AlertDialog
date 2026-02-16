package com.example.alertdialogdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class WeatherBriefFragment : Fragment() {
    
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
        val city = arguments?.getString(ARG_CITY) ?: "Moscow"
        view.findViewById<TextView>(R.id.tv_city).text = city
        return view
    }
}