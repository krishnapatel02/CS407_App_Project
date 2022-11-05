package com.example.simpleweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.math.floor


class MainActivity : AppCompatActivity() {
    private lateinit var temperatureText:TextView
    private lateinit var windText:TextView
    private lateinit var weatherImage:ImageView
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var search:SearchView
    private lateinit var dateText:TextView
    private lateinit var feelslike:TextView
    private lateinit var city:TextView
    private lateinit var button:Button
    private var temperature = 0.0
    private var celcius = ""

    var API_KEY = "caa39f0b7263fe9e809371d7ec15ef82"
    var weather_url = ""
    //var weather_city_url = "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {

        var units = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        temperatureText = findViewById(R.id.temperatureText)
        windText = findViewById(R.id.windSpeed)
        dateText = findViewById(R.id.date)
        feelslike = findViewById(R.id.feelslike)
        city = findViewById(R.id.city)
        button = findViewById(R.id.unitButton)
        button.setOnClickListener(){
            if(units == false){
                var test = temperature
                //Toast.makeText(this, test.toString(), Toast.LENGTH_SHORT).show()
                temperatureText.text = celcius
                units = true
            }else{
                temperatureText.text = toF(temperature)
                units = false
            }
        }


        val currentDate = LocalDate.now()
        val m = currentDate.month
        val day = currentDate.dayOfMonth
        dateText.text = m.toString() + " " + day.toString() + ", "
        weatherImage = findViewById(R.id.weatherImage)
        search= findViewById(R.id.search)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return

        }
        fusedLocationClient.lastLocation.addOnSuccessListener{location: Location? ->
            weather_url = "https://api.openweathermap.org/data/2.5/weather?lat=" + location?.latitude + "&lon=" + location?.longitude + "&appid=" + API_KEY + "&units=imperial"
            val geocoder = Geocoder(this, Locale.getDefault())
            var lat: Double? = location?.latitude?.toDouble()
            var lon: Double? = location?.longitude?.toDouble()
            var addresses: List<Address> = geocoder.getFromLocation(lat!!, lon!!, 1)
            var cityName = addresses.get(0).getAddressLine(0)
            var state = addresses.get(0).getAddressLine(1)
            city.text = cityName


            displayData(weather_url)


        }



    }

    fun displayData(url: String){
        val queue = Volley.newRequestQueue(this)
        Log.e("lat", url)
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                // get the JSON object
                val obj = JSONObject(response.toString())
                val main = obj.getJSONObject("main")
                temperature = main.getString("temp").toDouble()
                val degree = obj.getJSONObject("wind").getString("deg")
                temperatureText.text = toF(temperature)
                celcius = toC(temperature)
                windText.text = "Wind speed: " + obj.getJSONObject("wind").getString("speed") + " mph " + windDirection(degree.toInt())
                feelslike.text = "Feels like: " + toF(main.getString("feels_like").toDouble())

                // get the Array from obj of name - "data"


                // set the temperature and the city
                // name using getString() function
            },
            // In case of any error
            { println("Error") })
        queue.add(stringReq)

    }
    fun toF(temp: Double): String {
        val num = String.format("%.1f", temp).toDouble()
        return num.toString() + " °F"
    }

    fun toC(temp: Double): String {
        val tempC = (temp - 32)*(5/9)
        val num = String.format("%.1f", tempC).toDouble()
        return num.toString() + " °C"
    }

    fun toCnum(tempF: Double): Double {
        val tempC = (tempF - 32)*(5/9)
        return tempC
    }
    fun toFnum(tempC: Double): Double {
        val tempF = (tempC *(9/5))+32
        return tempF
    }
    fun windDirection(degrees: Int): String{
        var num = floor((degrees / 22.5) + 0.5);
        var arr = arrayOf<String>("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        return arr[(num % 16).toInt()];
    }
}
