package com.example.evmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.evmate.databinding.ItemStationListBinding
import com.example.evmate.model.Station

class StationListAdapter(
    private var stations: List<Station>,
    private val onClick: (Station) -> Unit
) : RecyclerView.Adapter<StationListAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val binding = ItemStationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StationViewHolder(binding)
    }

    override fun getItemCount() = stations.size

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    inner class StationViewHolder(private val binding: ItemStationListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(station: Station) {
            binding.tvName.text = station.name
            binding.tvAddress.text = station.address
            binding.tvAvailability.text = if (station.available) "Currently available" else "Unavailable"
            binding.tvAvailability.setTextColor(
                binding.root.resources.getColor(
                    if (station.available) R.color.green_600 else R.color.red_600, null
                )
            )
            binding.tvDistance.text = "${"%.1f".format(station.distanceKm)} Km"
            binding.root.setOnClickListener { onClick(station) }
        }
    }
}