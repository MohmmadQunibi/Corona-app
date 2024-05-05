package com.itclub.covid19tracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.itclub.covid19tracker.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var selectedItem: String? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val countryFlag = findViewById<ImageView>(R.id.country_flag)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        val countriesMenu = findViewById<AutoCompleteTextView>(R.id.countries_menu)
        val countriesT = resources.getStringArray(R.array.Countries)
        val adapterT = ArrayAdapter(this, R.layout.list_countries, countriesT)
        countriesMenu.setAdapter(adapterT)
        countriesMenu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                selectedItem = p0.toString()
                binding.progressBar.visibility = View.VISIBLE
                binding.countrySV.visibility = View.GONE
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

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        countriesMenu.setText(countriesT[0], false)
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
                binding.globalNumber.text = allNumbers
                binding.lastUpdate.text = lastUpdate
                binding.countryCases.text = countryCase
                binding.progressBar.visibility = View.GONE
                binding.countrySV.visibility = View.VISIBLE
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
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.action_refresh -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.countrySV.visibility = View.INVISIBLE
                getData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}