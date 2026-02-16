package com.example.alertdialogdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), MyDialog.DialogListener {

    private var currentCity = ""
    private var currentDesignIndex = 0 // 0: Brief, 1: Detailed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (currentCity.isEmpty()) {
            currentCity = resources.getStringArray(R.array.cities)[0]
        }

        if (savedInstanceState == null) {
            updateFragment()
        }

        findViewById<Button>(R.id.btn_change_view).apply {
            text = getString(R.string.btn_settings)
            setOnClickListener {
                val dialog = MyDialog.newInstance(MyDialog.DialogType.SETTINGS)
                dialog.setListener(this@MainActivity)
                dialog.show(supportFragmentManager, "settings")
            }
        }
    }

    override fun onDesignSelected(index: Int) {
        currentDesignIndex = index
        updateFragment()
    }

    override fun onCitySelected(city: String) {
        currentCity = city
        updateFragment()
    }

    private fun updateFragment() {
        val fragment: Fragment = if (currentDesignIndex == 0) {
            WeatherBriefFragment.newInstance(currentCity)
        } else {
            WeatherDetailedFragment.newInstance(currentCity)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}