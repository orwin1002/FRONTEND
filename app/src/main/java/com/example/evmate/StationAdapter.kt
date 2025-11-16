package com.example.evmate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class StationAdapter(
    private val context: Context,
    private var items: List<StationUiItem>,
    private val favoriteIds: MutableSet<String>,
    private val onToggleFavoritePersist: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_STATION = 1
    }

    fun updateData(newItems: List<StationUiItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is StationUiItem.Header -> TYPE_HEADER
            is StationUiItem.StationRow -> TYPE_STATION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_station, parent, false)
            StationHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StationUiItem.Header -> (holder as HeaderHolder).bind(item.title)
            is StationUiItem.StationRow -> (holder as StationHolder).bind(item.station, favoriteIds.contains(item.station.id))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv: TextView = itemView.findViewById(android.R.id.text1)
        fun bind(text: String) {
            tv.text = text
            tv.setPadding(16, 8, 16, 8)
        }
    }

    inner class StationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvEnergy: TextView = itemView.findViewById(R.id.tvEnergy)
        private val tvCo2: TextView = itemView.findViewById(R.id.tvCo2)
        private val chipScore: Chip = itemView.findViewById(R.id.chipScore)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
        private val ivMap: ImageView = itemView.findViewById(R.id.ivMapPreview)

        // Perks
        private val perkParking: TextView = itemView.findViewById(R.id.perkParking)
        private val perkCafe: TextView = itemView.findViewById(R.id.perkCafe)
        private val perkService: TextView = itemView.findViewById(R.id.perkService)
        private val perkRestroom: TextView = itemView.findViewById(R.id.perkRestroom)

        fun bind(station: Station, isFavorite: Boolean) {
            tvName.text = station.name
            tvAddress.text = station.address

            tvEnergy.text = when (station.energyType) {
                EnergyType.SOLAR -> "☀️"
                EnergyType.WIND -> "🌀"
                EnergyType.GRID -> "⚡"
            }

            chipScore.text = "Green ${station.greenScore}"
            tvCo2.text = "~${String.format("%.1f", station.co2SavedKg)} kg CO₂ saved vs grid"

            perkParking.visibility = if (station.perkParking) View.VISIBLE else View.GONE
            perkCafe.visibility = if (station.perkCafe) View.VISIBLE else View.GONE
            perkService.visibility = if (station.perkService) View.VISIBLE else View.GONE
            perkRestroom.visibility = if (station.perkRestroom) View.VISIBLE else View.GONE

            btnFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled_24 else R.drawable.ic_favorite_border_24)
            btnFavorite.setOnClickListener {
                val nowFav = !favoriteIds.contains(station.id)
                if (nowFav) favoriteIds.add(station.id) else favoriteIds.remove(station.id)
                onToggleFavoritePersist(station.id, nowFav)
                notifyItemChanged(bindingAdapterPosition)
            }

            // Tap: expand/collapse map preview; Long-press: open maps
            itemView.setOnClickListener {
                ivMap.visibility = if (ivMap.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            itemView.setOnLongClickListener {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(station.name)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Maps not installed", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }
}

/* UI list model allowing a sticky header when favorites exist */
sealed class StationUiItem {
    data class Header(val title: String) : StationUiItem()
    data class StationRow(val station: Station) : StationUiItem()
}

/* Domain */
data class Station(
    val id: String,
    val name: String,
    val address: String,
    val energyType: EnergyType,
    val greenScore: Int,
    val co2SavedKg: Double,
    val perkParking: Boolean,
    val perkCafe: Boolean,
    val perkService: Boolean,
    val perkRestroom: Boolean
)

enum class EnergyType { SOLAR, WIND, GRID }