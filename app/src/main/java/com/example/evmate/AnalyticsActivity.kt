package com.example.evmate

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityAnalyticsBinding
import kotlin.math.roundToInt

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Demo metrics
        val total = 120
        val peakLabel = "5–7 pm"
        val utilization = 0.72f

        binding.tvTotalSessions.text = "Total sessions today: $total"
        binding.tvPeak.text = "Peak demand: $peakLabel"
        binding.piUtil.max = 100
        binding.piUtil.progress = (utilization * 100).roundToInt()
        binding.tvEnergySaved.text = "256 kWh saved this week"
        binding.tvEnergySaved.text = "256 kWh saved this week"
        binding.tvCarbon.text = "~12.4 kg CO₂ saved"
        binding.progressEnergy.progress = 65



        renderBars(intArrayOf(
            4,7,6,9,12,18,22,25,20,16,14,13, // morning → noon
            15,18,21,27,30,28,26,20,14,10,7,5 // afternoon/evening/night
        ))
    }

    private fun renderBars(values: IntArray) {
        val max = values.maxOrNull()?.coerceAtLeast(1) ?: 1
        val host = binding.barHost
        host.removeAllViews()
        val dp = resources.displayMetrics.density
        values.forEachIndexed { idx, v ->
            val bar = View(this).apply {
                setBackgroundColor(if (idx in 16..18) Color.parseColor("#2E7D32") else Color.parseColor("#A5D6A7"))
                layoutParams = LinearLayout.LayoutParams(0, (v * 100f / max * dp).toInt() + (8 * dp).toInt(), 1f).apply {
                    setMargins((1*dp).toInt(), 0, (1*dp).toInt(), 0)
                }
            }
            host.addView(bar)
        }
    }
}