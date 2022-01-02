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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class MainPage : AppCompatActivity() {
    private var totalText: TextView? = null
    private var countryCases: TextView? = null
    private var timeUpdate: TextView? = null
    private var selectedItem: String? = null
    private lateinit var countrySV: ScrollView
    private var worldMapImage: ImageView? = null
    private lateinit var mainPageProgressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        totalText = findViewById(R.id.global_number)
        countryCases = findViewById(R.id.country_cases)
        timeUpdate = findViewById(R.id.last_update)
        mainPageProgressBar = findViewById(R.id.progressBar)
        countrySV = findViewById(R.id.countrySV)
        worldMapImage = findViewById(R.id.world_map_image)
        val countryFlag = findViewById<ImageView>(R.id.country_flag)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        test()

        MobileAds.initialize(this)

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
                test()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

        val adView = findViewById<AdView>(R.id.mainAD)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun test() {
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
            val doc3 =
                Jsoup.connect("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_pandemic#/media/File:COVID-19_Outbreak_World_Map_per_Capita.svg")
                    .get()
            val number = doc1.getElementsByClass("maincounter-number")
            val cases = doc.getElementsByClass("maincounter-number")
            val lastUpdated = doc1.select("div.content-inner")
            val worldMap = doc3.select("img[src$=.png]")[2]
            val mapUrl = worldMap.absUrl("src")
            handler.post {
                allNumbers = number.text()
                countryCase = cases.text()
                lastUpdate = lastUpdated.text()
                lastUpdate = lastUpdate.substring(30, 69)
                allNumbers = allNumbers.replace(" ", "\n\n\n")
                countryCase = countryCase.replace(" ", "\n\n\n")
                totalText!!.text = allNumbers
                timeUpdate!!.text = lastUpdate
                countryCases!!.text = countryCase
                Picasso.get().load(mapUrl).into(worldMapImage)
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
                test()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}