package com.itclub.covid19tracker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class SettingsPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        val aboutApp = findViewById<Button>(R.id.about_app_button)
        val references = findViewById<Button>(R.id.references_button)
        val checkForUpdates = findViewById<Button>(R.id.check_for_updates_button)
        val donate = findViewById<Button>(R.id.donate)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        aboutApp.setOnClickListener {
            val manager = this.packageManager
            val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
            MaterialAlertDialogBuilder(this).setTitle("About")
                .setMessage("COVID-19 Tracker Version ${info.versionName}")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        references.setOnClickListener {
            val url = "https://www.worldometers.info/coronavirus/"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        checkForUpdates.setOnClickListener {
            Toast.makeText(this,"Checking for updates...", Toast.LENGTH_SHORT).show()
            checkForUpdate()
        }

        donate.setOnClickListener {
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
