package com.example.java_pro01.managers

import com.example.java_pro01.models.*

/**
 * Singleton manager class for handling Lutemon creation and management
 */
class LutemonManager private constructor() {
    private val lutemons = mutableListOf<Lutemon>()

    companion object {
        @Volatile
        private var instance: LutemonManager? = null

        fun getInstance(): LutemonManager {
            return instance ?: synchronized(this) {
                instance ?: LutemonManager().also { instance = it }
            }
        }
    }

    // Create new Lutemon
    fun createLutemon(name: String, color: String) {
        val lutemon = when (color.lowercase()) {
            "white" -> WhiteLutemon(name)
            "green" -> GreenLutemon(name)
            "pink" -> PinkLutemon(name)
            "orange" -> OrangeLutemon(name)
            "black" -> BlackLutemon(name)
            else -> throw IllegalArgumentException("Invalid Lutemon color")
        }
        lutemons.add(lutemon)
    }

    // Get all Lutemons in a specific location
    fun getLutemonsInLocation(location: String): List<Lutemon> {
        return lutemons.filter { it.getLocation() == location }
    }

    // Move Lutemon to a new location
    fun moveLutemon(lutemon: Lutemon, newLocation: String) {
        // 找到要移动的 Lutemon
        val targetLutemon = lutemons.find { it.name == lutemon.name }
        targetLutemon?.setLocation(newLocation)
    }

    // Get all Lutemons
    fun getAllLutemons(): List<Lutemon> = lutemons.toList()
}