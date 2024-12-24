package com.mauleCo.cornerpocket.Activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mauleCo.cornerpocket.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adjustFontScale(resources.configuration)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the ActionBar if needed
        supportActionBar?.hide()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

    }

    /**
     * Sets the maximum font size to be 1.3x the standard size
     */
    private fun adjustFontScale(configuration: Configuration) {
        Log.e("MA", "adjustFontScale")

        Log.e("MA", "fontScale = ${configuration.fontScale}")
        if (configuration.fontScale > 1.30) {
            Log.e("MA", "font too big. scale down...")

            configuration.fontScale = 1.30f
            val metrics = resources.displayMetrics
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }

}