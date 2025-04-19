package com.example.java_pro01.models

/**
 * White Lutemon - Balanced Type
 */
class WhiteLutemon(name: String) : Lutemon(
    name = name,
    color = "White",
    attack = 5,
    defense = 4,
    maxHealth = 20,
    experience = 0
)

/**
 * Green Lutemon - Defensive Type
 */
class GreenLutemon(name: String) : Lutemon(
    name = name,
    color = "Green",
    attack = 6,
    defense = 3,
    maxHealth = 19,
    experience = 0
)

/**
 * Pink Lutemon - Life Type
 */
class PinkLutemon(name: String) : Lutemon(
    name = name,
    color = "Pink",
    attack = 7,
    defense = 2,
    maxHealth = 18,
    experience = 0
)

/**
 * Orange Lutemon - Attack Type
 */
class OrangeLutemon(name: String) : Lutemon(
    name = name,
    color = "Orange",
    attack = 8,
    defense = 1,
    maxHealth = 17,
    experience = 0
)

/**
 * Black Lutemon - Extreme Type
 */
class BlackLutemon(name: String) : Lutemon(
    name = name,
    color = "Black",
    attack = 9,
    defense = 0,
    maxHealth = 16,
    experience = 0
)