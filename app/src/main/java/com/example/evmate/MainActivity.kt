package com.example.evmate

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evmate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StationAdapter

    private val prefs by lazy { getSharedPreferences("evmate_prefs", Context.MODE_PRIVATE) }
    private val favoriteIds: MutableSet<String> by lazy {
        prefs.getStringSet("favorites", emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    private var greenStreakDays: Int
        get() = prefs.getInt("green_streak_days", 5) // demo default
        set(value) { prefs.edit().putInt("green_streak_days", value).apply() }

    // Bengaluru sample data with perks + green stats (15)
    private val allStations: List<Station> by lazy {
        listOf(
            Station("1", "UB City Solar Hub", "Vittal Mallya Rd, Ashok Nagar, Bengaluru", EnergyType.SOLAR, 92, 3.4, true, true, false, true),
            Station("2", "MG Road Grid Charge", "Mahatma Gandhi Rd, Bengaluru", EnergyType.GRID, 40, 0.2, false, true, true, true),
            Station("3", "Indiranagar Wind Port", "100 Feet Rd, Indiranagar, Bengaluru", EnergyType.WIND, 88, 2.7, true, true, false, false),
            Station("4", "Koramangala Solar Bay", "80 Feet Rd, Koramangala 4th Block, Bengaluru", EnergyType.SOLAR, 86, 2.5, true, true, true, true),
            Station("5", "HSR Layout Charge Point", "27th Main Rd, HSR Sector 2, Bengaluru", EnergyType.GRID, 55, 0.6, true, false, true, true),
            Station("6", "Whitefield Energy Plaza", "ITPL Main Rd, Whitefield, Bengaluru", EnergyType.GRID, 50, 0.5, true, true, true, false),
            Station("7", "Electronic City Wind Hub", "Hosur Rd, Electronic City Phase 1, Bengaluru", EnergyType.WIND, 82, 2.1, true, false, false, true),
            Station("8", "Hebbal Lakeside Solar", "Outer Ring Rd, Hebbal, Bengaluru", EnergyType.SOLAR, 90, 3.0, false, true, false, true),
            Station("9", "Yelahanka Green Port", "Doddaballapur Rd, Yelahanka, Bengaluru", EnergyType.WIND, 79, 1.9, true, false, true, false),
            Station("10", "Airport Road Supercharge", "Kempegowda Int'l Airport Rd, Devanahalli", EnergyType.GRID, 52, 0.4, true, true, true, true),
            Station("11", "Jayanagar Solar Court", "11th Main, 4th T Block, Jayanagar", EnergyType.SOLAR, 87, 2.6, false, true, false, true),
            Station("12", "JP Nagar Wind Lane", "24th Main Rd, JP Nagar 2nd Phase", EnergyType.WIND, 84, 2.3, true, true, false, false),
            Station("13", "Banashankari Grid Spot", "KR Rd, Banashankari Temple Ward", EnergyType.GRID, 48, 0.3, true, false, true, true),
            Station("14", "BTM Layout Eco Station", "Outer Ring Rd, BTM 2nd Stage", EnergyType.SOLAR, 89, 2.9, false, true, true, true),
            Station("15", "Bannerghatta Green Point", "Bannerghatta Rd, Arekere", EnergyType.WIND, 81, 2.0, true, false, true, false)
        )
    }

    private var currentQuery: String = ""
    private var greenOnly: Boolean = false
    private val selectedEnergyTypes: MutableSet<EnergyType> = mutableSetOf()
    private val selectedPerks: MutableSet<Perk> = mutableSetOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topAppBar)

        setupStreakUi()
        setupRecycler()
        setupSearch()
        setupEnergyChips()
        setupPerkChips()
        setupFab()

        applyFilters()
    }

    private fun setupStreakUi() {
        val days = greenStreakDays
        binding.tvStreak.text = "Green streak: $days days 🌱"
        binding.piStreak.isIndeterminate = false
        binding.piStreak.max = 7
        binding.piStreak.progress = days % 7
    }

    private fun setupRecycler() {
        adapter = StationAdapter(this, emptyList(), favoriteIds) { id, isFav ->
            val set = favoriteIds.toMutableSet()
            if (isFav) set.add(id) else set.remove(id)
            prefs.edit().putStringSet("favorites", set).apply()
            applyFilters()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString().orEmpty()
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGreenOnly.setOnCheckedChangeListener { _, isChecked ->
            greenOnly = isChecked
            applyFilters()
        }
    }

    private fun setupEnergyChips() {
        binding.chipSolar.setOnCheckedChangeListener { _, checked ->
            if (checked) selectedEnergyTypes.add(EnergyType.SOLAR) else selectedEnergyTypes.remove(EnergyType.SOLAR)
            applyFilters()
        }
        binding.chipWind.setOnCheckedChangeListener { _, checked ->
            if (checked) selectedEnergyTypes.add(EnergyType.WIND) else selectedEnergyTypes.remove(EnergyType.WIND)
            applyFilters()
        }
        binding.chipGrid.setOnCheckedChangeListener { _, checked ->
            if (checked) selectedEnergyTypes.add(EnergyType.GRID) else selectedEnergyTypes.remove(EnergyType.GRID)
            applyFilters()
        }
    }

    private fun setupPerkChips() {
        binding.chipPerkParking.setOnCheckedChangeListener { _, checked ->
            togglePerk(Perk.PARKING, checked); applyFilters()
        }
        binding.chipPerkCafe.setOnCheckedChangeListener { _, checked ->
            togglePerk(Perk.CAFE, checked); applyFilters()
        }
        binding.chipPerkService.setOnCheckedChangeListener { _, checked ->
            togglePerk(Perk.SERVICE, checked); applyFilters()
        }
        binding.chipPerkRestroom.setOnCheckedChangeListener { _, checked ->
            togglePerk(Perk.RESTROOM, checked); applyFilters()
        }
    }

    private fun togglePerk(perk: Perk, checked: Boolean) {
        if (checked) selectedPerks.add(perk) else selectedPerks.remove(perk)
    }

    private fun setupFab() {
        binding.fabMap.setOnClickListener {
            Toast.makeText(this, "Map feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFilters() {
        val q = currentQuery.trim().lowercase()

        val filtered = allStations.filter { s ->
            val matchesQuery = q.isEmpty() || s.name.lowercase().contains(q) || s.address.lowercase().contains(q)

            val isGreen = s.energyType == EnergyType.SOLAR || s.energyType == EnergyType.WIND
            val matchesGreen = if (greenOnly) isGreen else true

            val matchesEnergyChip = if (selectedEnergyTypes.isEmpty()) true else selectedEnergyTypes.contains(s.energyType)

            val matchesPerks = if (selectedPerks.isEmpty()) true else {
                selectedPerks.all { perk ->
                    when (perk) {
                        Perk.PARKING -> s.perkParking
                        Perk.CAFE -> s.perkCafe
                        Perk.SERVICE -> s.perkService
                        Perk.RESTROOM -> s.perkRestroom
                    }
                }
            }

            matchesQuery && matchesGreen && matchesEnergyChip && matchesPerks
        }

        // Build UI list with sticky favorites header when any favorites in result
        val favSet = favoriteIds
        val favorites = filtered.filter { favSet.contains(it.id) }
        val others = filtered.filterNot { favSet.contains(it.id) }

        val uiItems = mutableListOf<StationUiItem>()
        if (favorites.isNotEmpty()) {
            uiItems.add(StationUiItem.Header("Favorites"))
            uiItems.addAll(favorites.map { StationUiItem.StationRow(it) })
            if (others.isNotEmpty()) uiItems.add(StationUiItem.Header("All stations"))
        }
        uiItems.addAll(others.map { StationUiItem.StationRow(it) })

        adapter.updateData(uiItems)
    }
}

enum class Perk { PARKING, CAFE, SERVICE, RESTROOM }