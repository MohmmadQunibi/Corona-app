package com.itclub.covid19tracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class MainPage : AppCompatActivity() {
    private var totalText: TextView? = null
    private var countryCases: TextView? = null
    private var timeUpdate: TextView? = null
    private var selectedItem: String? = null
    private lateinit var countrySV: ScrollView
    private lateinit var mainPageProgressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        totalText = findViewById(R.id.global_number)
        countryCases = findViewById(R.id.country_cases)
        timeUpdate = findViewById(R.id.last_update)
        mainPageProgressBar = findViewById(R.id.progressBar)
        countrySV = findViewById(R.id.countrySV)
        val countryFlag = findViewById<ImageView>(R.id.country_flag)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val countrySpinner = findViewById<Spinner>(R.id.country_spinner)
        val countries = resources.getStringArray(R.array.Countries)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                // your code here
                val item = parentView.getItemAtPosition(position)
                selectedItem = item.toString()
                mainPageProgressBar.visibility = View.VISIBLE
                countrySV.visibility = View.GONE
                when (selectedItem) {
                    "Jordan" -> countryFlag.setImageResource(R.drawable.jordan_flag)
                    "United-Arab-Emirates" -> countryFlag.setImageResource(R.drawable.uae_flag)
                    "Iraq" -> countryFlag.setImageResource(R.drawable.iraq_flag)
                    "Qatar" -> countryFlag.setImageResource(R.drawable.qatar_flag)
                    "Saudi-Arabia" -> countryFlag.setImageResource(R.drawable.ksa_flag)
                    "Lebanon" -> countryFlag.setImageResource(R.drawable.lebanon_flag)
                    "State-of-Palestine" -> countryFlag.setImageResource(R.drawable.palastine_flag)
                    "Egypt" -> countryFlag.setImageResource(R.drawable.egypt_flag)
                    "Bahrain" -> countryFlag.setImageResource(R.drawable.bahrain_flag)
                    "Morocco" -> countryFlag.setImageResource(R.drawable.morroco_flag)
                    "Libya" -> countryFlag.setImageResource(R.drawable.libya_flag)
                    "Algeria" -> countryFlag.setImageResource(R.drawable.algeria_flag)
                    "Turkey" -> countryFlag.setImageResource(R.drawable.turkey_flag)
                }
                getData()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun getData() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(
            Looper.getMainLooper()
        )
        executor.execute {
            var allNumbers: String
            var countryCase: String
            var lastUpdate: String
            val doc =
                Jsoup.connect("https://www.worldometers.info/coronavirus/country/$selectedItem/")
                    .get()
            val doc1 = Jsoup.connect("https://www.worldometers.info/coronavirus/").get()
            val number = doc1.getElementsByClass("maincounter-number")
            val cases = doc.getElementsByClass("maincounter-number")
            val lastUpdated = doc1.select("#page-top + div")
            handler.post {
                allNumbers = number.text()
                countryCase = cases.text()
                lastUpdate = lastUpdated.text()
                allNumbers = allNumbers.replace(" ", "\n\n\n")
                countryCase = countryCase.replace(" ", "\n\n\n")
                totalText!!.text = allNumbers
                timeUpdate!!.text = lastUpdate
                countryCases!!.text = countryCase
                mainPageProgressBar.visibility = View.GONE
                countrySV.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.corona_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsPage::class.java)
                startActivity(intent)
            }
            R.id.action_refresh -> {
                mainPageProgressBar.visibility = View.VISIBLE
                countrySV.visibility = View.INVISIBLE
                Toast.makeText(this, "updating data...", Toast.LENGTH_LONG).show()
                getData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}