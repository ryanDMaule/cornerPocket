package com.example.cornerpocket

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cornerpocket.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //region NAV BAR SET UP

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.playFragment, R.id.statsFragment, R.id.historyFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.hide()
        navView.setupWithNavController(navController)

        //HIDE BOTTOM NAV ON X FRAGMENTS
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.opponentSelectFragment ||
                destination.id == R.id.gameTypeFragment ||
                destination.id == R.id.breakSelectionFragment ||
                destination.id == R.id.gameUnderwayFragment ||
                destination.id == R.id.gameReviewFragment ||
                destination.id == R.id.gameDetailsFragment) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        //endregion

    }
}