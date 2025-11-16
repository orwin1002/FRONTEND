package com.example.evmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evmate.databinding.ActivityReservationBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationBinding
    private lateinit var adapter: SlotAdapter
    private val prefs by lazy { getSharedPreferences("evmate_reservations", Context.MODE_PRIVATE) }

    private val dailyTemplate: List<Slot> by lazy {
        // 30‑min windows 8:00–22:00, capacity per window = 6
        val list = mutableListOf<Slot>()
        for (h in 8..21) {
            list += Slot("$h:00", "$h:30", 6, 0, false)
            list += Slot("$h:30", "${h + 1}:00", 6, 0, false)
        }
        list
    }

    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDate()
        setupWindows()
        setupList()
        binding.btnMyBookings.setOnClickListener { showMyBookings() }

        refreshForSelected()
    }

    private fun setupDate() {
        val today = Calendar.getInstance()
        binding.etDate.setText(dateFmt.format(today.time))
        binding.tilDate.setEndIconOnClickListener {
            val c = Calendar.getInstance()
            val dp = android.app.DatePickerDialog(this, { _, y, m, d ->
                c.set(y, m, d)
                binding.etDate.setText(dateFmt.format(c.time))
                refreshForSelected()
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
            dp.show()
        }
    }

    private fun setupWindows() {
        val windows = listOf("All", "Morning", "Afternoon", "Evening", "Night")
        windows.forEachIndexed { i, w ->
            val chip = Chip(this).apply {
                text = w
                isCheckable = true
                isChecked = i == 0
            }
            binding.chipsWindows.addView(chip)
        }
        binding.chipsWindows.setOnCheckedStateChangeListener { _, _ -> refreshForSelected() }
    }

    private fun setupList() {
        adapter = SlotAdapter(onToggle = { slot ->
            val dateKey = binding.etDate.text.toString()
            val key = "$dateKey|${slot.start}-${slot.end}"
            val booked = prefs.getBoolean(key, false)
            if (booked) {
                prefs.edit().remove(key).apply()
                Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putBoolean(key, true).apply()
                scheduleReminder(dateKey, slot)
                Toast.makeText(this, "Reserved ${slot.start}-${slot.end}", Toast.LENGTH_SHORT).show()
            }
            refreshForSelected()
        })
        binding.rvSlots.layoutManager = LinearLayoutManager(this)
        binding.rvSlots.adapter = adapter
    }

    private fun filterByWindow(all: List<Slot>): List<Slot> {
        val label = binding.chipsWindows.children
            .map { it as Chip }
            .firstOrNull { it.isChecked }?.text?.toString() ?: "All"

        fun hour(s: String) = s.substringBefore(":").toInt()
        return when (label) {
            "Morning" -> all.filter { hour(it.start) in 8..11 }
            "Afternoon" -> all.filter { hour(it.start) in 12..16 }
            "Evening" -> all.filter { hour(it.start) in 17..19 }
            "Night" -> all.filter { hour(it.start) >= 20 }
            else -> all
        }
    }

    private fun refreshForSelected() {
        val dateKey = binding.etDate.text.toString()
        // compute occupancy from prefs (in-memory demo)
        val populated = dailyTemplate.map { base ->
            val key = "$dateKey|${base.start}-${base.end}"
            base.copy(
                booked = prefs.getBoolean(key, false),
                occupied = if (prefs.getBoolean(key, false)) 1 else 0
            )
        }
        adapter.submitList(filterByWindow(populated))
    }

    private fun showMyBookings() {
        val dateKey = binding.etDate.text.toString()
        val mine = dailyTemplate.filter {
            prefs.getBoolean("$dateKey|${it.start}-${it.end}", false)
        }.joinToString("\n") { "${it.start}-${it.end}" }
        Toast.makeText(this, if (mine.isEmpty()) "No bookings today" else "Your slots:\n$mine", Toast.LENGTH_LONG).show()
    }

    private fun scheduleReminder(dateKey: String, slot: Slot) {
        // Reminder 10 minutes before start
        val (h, m) = slot.start.split(":").map { it.toInt() }
        val cal = Calendar.getInstance().apply {
            time = dateFmt.parse(dateKey)!!
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            add(Calendar.MINUTE, -10)
        }
        val intent = Intent(this, ReminderReceiver::class.java)
            .putExtra("title", "Charging in 10 min")
            .putExtra("msg", "Your ${slot.start}-${slot.end} slot starts soon")
        val pi = PendingIntent.getBroadcast(
            this, (dateKey + slot.start).hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExact(
            AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi
        )
    }
}

data class Slot(
    val start: String,
    val end: String,
    val capacity: Int,
    val occupied: Int,
    val booked: Boolean
) {
    val free: Int get() = (capacity - occupied).coerceAtLeast(0)
}

class SlotAdapter(
    private val onToggle: (Slot) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Slot, SlotVH>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Slot>() {
        override fun areItemsTheSame(o: Slot, n: Slot) = o.start == n.start && o.end == n.end
        override fun areContentsTheSame(o: Slot, n: Slot) = o == n
    }
) {
    override fun onCreateViewHolder(p: android.view.ViewGroup, v: Int): SlotVH {
        val vroot = android.view.LayoutInflater.from(p.context).inflate(R.layout.item_slot, p, false)
        return SlotVH(vroot, onToggle)
    }
    override fun onBindViewHolder(h: SlotVH, pos: Int) = h.bind(getItem(pos))
}

class SlotVH(
    itemView: android.view.View,
    private val onToggle: (Slot) -> Unit
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    private val tvTime = itemView.findViewById<android.widget.TextView>(R.id.tvTime)
    private val tvAvail = itemView.findViewById<android.widget.TextView>(R.id.tvAvail)
    private val btn = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAction)

    fun bind(s: Slot) {
        tvTime.text = "${s.start} - ${s.end}"
        tvAvail.text = "${s.free} of ${s.capacity} free"
        val booked = s.booked
        btn.text = if (booked) "Cancel" else "Reserve"
        btn.setIconResource(if (booked) android.R.drawable.ic_menu_close_clear_cancel else android.R.drawable.ic_input_add)
        btn.setOnClickListener { onToggle(s) }
        itemView.alpha = if (s.free == 0 && !booked) 0.5f else 1f
    }
}