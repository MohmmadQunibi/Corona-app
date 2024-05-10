package com.itclub.covid19tracker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.itclub.covid19tracker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.settingsToolbar.toolbar)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewSettings.loadAd(adRequest)

        binding.aboutAppButton.setOnClickListener {
            val manager = this.packageManager
            val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
            MaterialAlertDialogBuilder(this).setTitle("About")
                .setMessage("COVID-19 Tracker Version ${info.versionName}")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        binding.referencesButton.setOnClickListener {
            val url = "https://www.worldometers.info/coronavirus/"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        binding.checkForUpdatesButton.setOnClickListener {
            Toast.makeText(this, "Checking for updates...", Toast.LENGTH_SHORT).show()
            checkForUpdate()
        }

        binding.donate.setOnClickListener {
            val url = "https://covid19responsefund.org/en/"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }
    }

    private fun goToUrl() {
        val uriUrl = Uri.parse("https://covid-19-tracker.en.uptodown.com/android")
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun checkForUpdate() {
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("versions").document("version")
        docRef.get()
            .addOnSuccessListener { document ->
                val latestVersion = document.getString("latest_version")
                if (latestVersion.equals(info.versionName)) {
                    MaterialAlertDialogBuilder(this).setMessage("COVID-19 Tracker is up to date")
                        .setCancelable(false)
                        .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                        .create().show()
                } else {
                    MaterialAlertDialogBuilder(this).setTitle("Update").setMessage(
                        "There is a new version available\n" +
                                "Current version V${info.versionName}\n" +
                                "New version V$latestVersion"
                    )
                        .setCancelable(true)
                        .setPositiveButton("Update") { _, _ -> goToUrl() }
                        .create().show()
                }
            }
    }
}
