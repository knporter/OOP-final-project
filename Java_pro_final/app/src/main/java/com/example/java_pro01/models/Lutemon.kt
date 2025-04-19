package com.example.java_pro01.models

/**
 * Lutemon基类，定义所有Lutemon角色的基本属性和行为
 */
abstract class Lutemon(
    val name: String,
    val color: String,
    private var attack: Int,
    private var defense: Int,
    private var maxHealth: Int,
    private var experience: Int = 0,
    var currentHealth: Int = 0,
    private var location: String = "Home"
) {
    // 统计数据
    private var battleCount: Int = 0
    private var wins: Int = 0
    private var trainingCount: Int = 0
    private var trainingChances: Int = 0

    init {
        // 从基础属性表中获取初始值
        val baseStats = LutemonStatsProvider.lutemonBaseStats[color] ?: throw IllegalArgumentException("Invalid color")
        attack = baseStats.attack
        defense = baseStats.defense
        maxHealth = baseStats.maxHealth
        experience = baseStats.experience
        
        // 设置初始生命值为最大生命值
        if (currentHealth == 0) {
            currentHealth = maxHealth
        }
    }

    // Getters for stats
    fun getAttack() = attack
    fun getDefense() = defense
    fun getMaxHealth() = maxHealth
    fun getExperience() = experience
    fun getLocation() = location

    // Getters for statistics
    fun getBattleCount() = battleCount
    fun getWins() = wins
    fun getWinRate() = if (battleCount > 0) wins.toFloat() / battleCount else 0f
    fun getTrainingCount() = trainingCount
    fun getTrainingChances() = trainingChances

    // Statistics update methods
    fun recordBattle(won: Boolean) {
        battleCount++
        if (won) wins++
        resetTrainingChances()
    }

    fun recordTraining() {
        trainingCount++
    }

    // 提升属性的方法
    fun improveMaxHealth(amount: Int) {
        maxHealth += amount
        currentHealth += amount  // 同时提升当前血量
    }

    fun improveAttack(amount: Int) {
        attack += amount
    }

    fun improveDefense(amount: Int) {
        defense += amount
    }

    // Experience management
    fun addExperience(amount: Int) {
        experience = maxOf(0, experience + amount)  // 确保经验值不会低于0
    }

    // Health management
    fun heal(amount: Int) {
        currentHealth = minOf(currentHealth + amount, maxHealth)
    }

    fun takeDamage(damage: Int) {
        currentHealth = maxOf(0, currentHealth - damage)
    }

    fun isAlive() = currentHealth > 0

    // Location management
    fun setLocation(newLocation: String) {
        location = newLocation
        if (location == "Home") {
            heal(maxHealth)  // 回到家时完全恢复
        }
    }

    fun resetTrainingChances() {
        trainingChances = 2
    }

    fun useTrainingChance(): Boolean {
        return if (trainingChances > 0) {
            trainingChances--
            true
        } else {
            false
        }
    }

    // 战斗信息
    override fun toString(): String {
        return "$name ($color) - ATK:$attack DEF:$defense HP:$currentHealth/$maxHealth XP:$experience"
    }
}