package com.example.evmate

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    // The 15 station names for dropdown autocomplete
    private val stationNames = listOf(
        "UB City Solar Hub",
        "MG Road Grid Charge",
        "Indiranagar Wind Port",
        "Koramangala Solar Bay",
        "HSR Layout Charge Point",
        "Whitefield Energy Plaza",
        "Electronic City Wind Hub",
        "Hebbal Lakeside Solar",
        "Yelahanka Green Port",
        "Airport Road Supercharge",
        "Jayanagar Solar Court",
        "JP Nagar Wind Lane",
        "Banashankari Grid Spot",
        "BTM Layout Eco Station",
        "Bannerghatta Green Point"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Autocomplete dropdown for station search
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            stationNames
        )
        binding.etSearchDash.setAdapter(adapter)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // TODO: load your home/default fragment here
                    true
                }
                R.id.menu_stations -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Show Nearby Stations by default when Dashboard opens
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, StationListFragment())
            .commit()

        binding.btnReservation.setOnClickListener {
            startActivity(Intent(this, ReservationActivity::class.java))
        }

        binding.btnAnalytics.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }
    }
}