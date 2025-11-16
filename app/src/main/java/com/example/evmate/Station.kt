package com.example.evmate.model

import java.io.Serializable

data class Station(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val available: Boolean,
    val timing: String,
    val type: String,
    val power: String,
    val imageRes: Int? = null // for station detail
) : Serializable