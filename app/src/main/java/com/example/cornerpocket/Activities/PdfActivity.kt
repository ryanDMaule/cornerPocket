package com.example.cornerpocket.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.ActivityMainBinding
import com.example.cornerpocket.databinding.ActivityPdfBinding
import java.io.IOException

class PdfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the ActionBar if needed
        supportActionBar?.hide()

            try {
                binding.pdfView.fromAsset("rules_summary.pdf").load()
            } catch (e: IOException) {
                // LOAD WEBPAGE
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://billiards.colostate.edu/resource_files/rules_summary.pdf"))
                startActivity(i)
            }

    }
}