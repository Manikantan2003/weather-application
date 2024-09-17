package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//ce11ab71daec19bd9282b75aa10a8cbb
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"ce11ab71daec19bd9282b75aa10a8cbb","metric")
        response.enqueue(object : Callback<Weatherapp>{
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windSpeed =responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min
                    binding.temp.text="$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text="Max temp $maxTemp °C "
                    binding.minTemp.text="Min temp $minTemp °C "
                    binding.humidity.text="$humidity%"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}sunRise"
                    binding.sunset.text="${time(sunSet)}sunSet"
                    binding.sea.text="$seaLevel hpa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text= date()
                        binding.cityName.text="$cityName"

                    //Log.d("TAG", "onResponse: $temperature")

                    changeImagesAccordingToWeatherCondition(condition)
                }
            }

            private fun date(): String {
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                return sdf.format((Date()))
            }
            private fun time(timestamp: Long): String {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                return sdf.format((Date(timestamp*1000)))
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        }

    private fun changeImagesAccordingToWeatherCondition(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView2.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView2.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView2.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }

        }

        binding.lottieAnimationView2.playAnimation()

    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}