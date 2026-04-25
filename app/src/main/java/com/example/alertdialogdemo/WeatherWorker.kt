package com.example.alertdialogdemo

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val ACTION_WEATHER_UPDATE = "com.example.alertdialogdemo.WEATHER_UPDATE"
        const val PREFS_NAME = "weather_prefs"
    }

    override fun doWork(): Result {
        val requestedCity = inputData.getString("city") ?: return Result.failure()
        Log.d("WeatherWorker", "--- Starting work for city: $requestedCity ---")

        val apiKey = "a21a613ad9b979d4548840b417b5bade"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)

        return try {
            val response = api.getWeather(requestedCity, apiKey).execute()
            if (response.isSuccessful && response.body() != null) {
                val weather = response.body()!!
                
                // Сохраняем данные в SharedPreferences, чтобы они были доступны всегда
                val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putFloat("${requestedCity}_temp", weather.main.temp.toFloat())
                editor.putInt("${requestedCity}_humidity", weather.main.humidity)
                editor.putFloat("${requestedCity}_wind", weather.wind.speed.toFloat())
                editor.putString("${requestedCity}_desc", weather.weather[0].description)
                editor.apply()

                val intent = Intent(ACTION_WEATHER_UPDATE).apply {
                    putExtra("city", requestedCity) 
                    putExtra("temp", weather.main.temp)
                    putExtra("humidity", weather.main.humidity)
                    putExtra("speed", weather.wind.speed)
                    putExtra("desc", weather.weather[0].description)
                    setPackage(applicationContext.packageName)
                }
                
                Log.d("WeatherWorker", "Broadcasting and saving update for $requestedCity: ${weather.main.temp}°C")
                applicationContext.sendBroadcast(intent)

                Result.success(workDataOf("last_city" to requestedCity))
            } else {
                Log.e("WeatherWorker", "Server error: ${response.code()}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Network exception: ${e.message}")
            Result.retry()
        }
    }
}