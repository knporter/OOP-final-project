package com.example.java_pro01.models

data class LutemonStats(
    val color: String,
    val maxHealth: Int,
    val attack: Int,
    val defense: Int,
    val experience: Int = 0
)

object LutemonStatsProvider {
    val lutemonBaseStats = mapOf(
        "White" to LutemonStats(
            color = "White",
            maxHealth = 20,
            attack = 5,
            defense = 10,
            experience = 0
        ),
        "Green" to LutemonStats(
            color = "Green",
            maxHealth = 15,
            attack = 7,
            defense = 15,
            experience = 0
        ),
        "Black" to LutemonStats(
            color = "Black",
            maxHealth = 10,
            attack = 10,
            defense = 7,
            experience = 0
        ),
        "Pink" to LutemonStats(
            color = "Pink",
            maxHealth = 13,
            attack = 6,
            defense = 10,
            experience = 0
        ),
        "Orange" to LutemonStats(
            color = "Orange",
            maxHealth = 17,
            attack = 7,
            defense = 11,
            experience = 0
        )
    )
} 