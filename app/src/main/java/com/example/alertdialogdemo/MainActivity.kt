package com.example.alertdialogdemo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Locale

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

        findViewById<Button>(R.id.btn_sync_weather).setOnClickListener {
            startWeatherChain()
        }
    }

    private fun startWeatherChain() {
        val cities = resources.getStringArray(R.array.cities)
        if (cities.isEmpty()) return

        val workManager = WorkManager.getInstance(applicationContext)
        
        // Create the first work request
        var continuation = workManager.beginWith(
            OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(workDataOf("city" to cities[0]))
                .build()
        )

        // Chain the rest of the cities
        for (i in 1 until cities.size) {
            val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(workDataOf("city" to cities[i]))
                .build()
            continuation = continuation.then(workRequest)
        }

        continuation.enqueue()
        Toast.makeText(this, "Syncing weather for ${cities.size} cities...", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.lang_ru -> {
                setLocale("ru")
                true
            }
            R.id.lang_en -> {
                setLocale("en")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        
        resources.updateConfiguration(config, resources.displayMetrics)
        
        recreate()
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