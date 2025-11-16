package com.example.evmate

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary placeholder UI while Maps is disabled
        val tv = TextView(this).apply {
            text = "Maps temporarily disabled.\nFeature coming soon."
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        setContentView(tv)

        /*
        // ===== MAPS CODE (disabled) =====
        import android.Manifest
        import android.content.pm.PackageManager
        import android.location.Geocoder
        import androidx.core.app.ActivityCompat
        import androidx.core.content.ContextCompat
        import android.widget.Button
        import android.widget.EditText
        import android.widget.Toast
        import com.google.android.gms.location.FusedLocationProviderClient
        import com.google.android.gms.location.LocationServices
        import com.google.android.gms.maps.CameraUpdateFactory
        import com.google.android.gms.maps.GoogleMap
        import com.google.android.gms.maps.OnMapReadyCallback
        import com.google.android.gms.maps.SupportMapFragment
        import com.google.android.gms.maps.model.*
        import kotlinx.coroutines.*
        import org.json.JSONObject
        import java.net.URL

        class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

            private lateinit var mMap: GoogleMap
            private lateinit var fusedLocationClient: FusedLocationProviderClient
            private var currentLocation: LatLng? = null
            private val apiKey = "YOUR_API_KEY_HERE"

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_maps)

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)

                val etDestination = findViewById<EditText>(R.id.etDestination)
                val btnFindRoute = findViewById<Button>(R.id.btnFindRoute)

                btnFindRoute.setOnClickListener {
                    val destination = etDestination.text.toString().trim()
                    if (destination.isNotEmpty()) {
                        getRouteToDestination(destination)
                    } else {
                        Toast.makeText(this, "Enter a destination", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onMapReady(googleMap: GoogleMap) {
                mMap = googleMap
                getUserLocation()
            }

            private fun getUserLocation() { ... }

            private fun getRouteToDestination(destinationName: String) { ... }

            private fun drawRoute(origin: LatLng, destination: LatLng) { ... }

            private fun getDirectionsUrl(origin: LatLng, destination: LatLng): String { ... }

            private fun parsePolyline(jsonObject: JSONObject): List<LatLng> { ... }

            private fun decodePolyline(encoded: String): List<LatLng> { ... }
        }
        // ===== END MAPS CODE =====
        */
    }
}