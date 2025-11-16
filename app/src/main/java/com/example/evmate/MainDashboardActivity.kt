package com.example.evmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityMainDashboardBinding
// import com.google.android.gms.maps.SupportMapFragment // <-- Commented out map import

class MainDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment: Commented out map
        /*
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MapFragment())
            .commit()
        */

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.menu_map -> { ... } // if you use it later
                R.id.menu_stations -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, StationListFragment())

                        .commit()
                    true
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ProfileFragment())

                        .commit()
                    true
                }
                else -> false
            }
        }

        binding.btnReservation.setOnClickListener {
            startActivity(Intent(this, ReservationActivity::class.java))
        }
    }
}