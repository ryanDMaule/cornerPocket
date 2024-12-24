package com.mauleCo.cornerpocket.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mauleCo.cornerpocket.databinding.ActivityPdfBinding

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
            } catch (e: Exception) {
                // LOAD WEBPAGE
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://billiards.colostate.edu/resource_files/rules_summary.pdf"))
                startActivity(i)
            }

    }
}