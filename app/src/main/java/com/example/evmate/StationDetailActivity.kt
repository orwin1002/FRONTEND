package com.example.evmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityStationDetailBinding
import com.example.evmate.model.Station

class StationDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val station = intent.getSerializableExtra("station") as? Station ?: return

        binding.imgStation.setImageResource(station.imageRes ?: R.drawable.ic_launcher_background)
        binding.tvName.text = station.name
        binding.tvAddress.text = station.address
        binding.tvTiming.text = station.timing
        binding.tvType.text = station.type
        binding.tvAvailable.text = if (station.available) "Currently available" else "Unavailable"
        binding.tvAvailable.setTextColor(
            getColor(if (station.available) R.color.green_600 else R.color.red_600)
        )
        // add more fields as you see fit!
    }
}