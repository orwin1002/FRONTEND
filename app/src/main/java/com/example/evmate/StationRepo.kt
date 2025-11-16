package com.example.evmate.model

import com.example.evmate.R

object StationRepo {
    val stations = listOf(
        Station(
            "1", "Shell Recharge Station", "Kashi Nagar, Kumaraswamy Layout", 0.25, true,
            "5:00 AM - 7:00 PM", "3kW DC", "2kW DC", R.drawable.station1
        ),
        Station(
            "2", "Tata Power Charging Station", "Jaraganahalli Village, Kanakapura Rd", 1.2, true,
            "Open 24x7", "7kW AC", "7kW AC", R.drawable.station2
        ),
        Station(
            "3", "Electric Vehicle Charging Station", "7th Block, Jayanagar", 2.8, false,
            "8:00 AM - 8:00 PM", "Unavailable", "", R.drawable.station3
        ),
        Station(
            "4", "Kazam Charging Station", "Outer Ring Rd, JP Nagar", 1.7, true,
            "10:00 AM - 12:00 PM", "3kW DC", "", R.drawable.station4
        )
    )
}