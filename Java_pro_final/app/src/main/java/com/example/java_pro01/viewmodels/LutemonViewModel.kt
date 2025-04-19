package com.example.java_pro01.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.java_pro01.battle.BattleResult
import com.example.java_pro01.battle.BattleSystem
import com.example.java_pro01.managers.LutemonManager
import com.example.java_pro01.models.Lutemon
import com.example.java_pro01.training.TrainingSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for Lutemon game, responsible for managing UI state and handling user interactions
 */
class LutemonViewModel : ViewModel() {
    private val lutemonManager = LutemonManager.getInstance()
    private var battleSystem: BattleSystem? = null
    private val trainingSystem = TrainingSystem()

    // UI State
    private val _uiState = MutableStateFlow(LutemonUiState())
    val uiState: StateFlow<LutemonUiState> = _uiState.asStateFlow()

    // Create new Lutemon
    fun createLutemon(name: String, color: String) {
        viewModelScope.launch {
            try {
                lutemonManager.createLutemon(name, color)
                updateLutemonLists()
            } catch (e: Exception) {
                updateErrorMessage(e.message ?: "Failed to create Lutemon")
            }
        }
    }

    // Move Lutemon to new location
    fun moveLutemon(lutemon: Lutemon, targetLocation: String) {
        viewModelScope.launch {
            try {
                lutemonManager.moveLutemon(lutemon, targetLocation)
                updateLutemonLists()
            } catch (e: Exception) {
                updateErrorMessage(e.message ?: "Failed to move Lutemon")
            }
        }
    }

    // Start training
    fun startTraining(lutemon: Lutemon) {
        viewModelScope.launch {
            try {
                if (lutemon.useTrainingChance()) {
                    val result = trainingSystem.train(lutemon)
                    _uiState.update { currentState ->
                        currentState.copy(
                            trainingLog = result.trainingLog
                        )
                    }
                    updateLutemonLists()
                } else {
                    updateErrorMessage("There is no training chance available. Please participate in a battle to reset training chances.")
                }
            } catch (e: Exception) {
                updateErrorMessage(e.message ?: "Training failed")
            }
        }
    }

    // Perform battle action
    fun performBattleAction(attacker: Lutemon, defender: Lutemon, action: String) {
        viewModelScope.launch {
            try {
                // 确保在战斗中的 Lutemon 是最新的状态
                val updatedAttacker = lutemonManager.getLutemonsInLocation("Battle")
                    .find { it.name == attacker.name } ?: attacker
                val updatedDefender = lutemonManager.getLutemonsInLocation("Battle")
                    .find { it.name == defender.name } ?: defender
                
                // 如果还没有创建 battleSystem 或者战斗参与者改变，创建新的 battleSystem
                if (battleSystem == null) {
                    battleSystem = BattleSystem(updatedAttacker, updatedDefender)
                }
                
                // 执行回合
                val battleEnded = battleSystem?.executeTurn(action) ?: false
                val currentLog = battleSystem?.getBattleLog() ?: emptyList()
                
                _uiState.update { currentState ->
                    currentState.copy(
                        battleLog = currentLog,
                        battleEnded = battleEnded
                    )
                }
                
                if (battleEnded) {
                    // Move both Lutemons back home when battle ends
                    moveLutemon(updatedAttacker, "Home")
                    moveLutemon(updatedDefender, "Home")
                    // Reset battle system
                    battleSystem = null
                }
                
                // 确保在每次行动后更新状态
                updateLutemonLists()
            } catch (e: Exception) {
                updateErrorMessage(e.message ?: "Battle action failed")
            }
        }
    }

    // Improve a random stat
    fun improveRandomStat(lutemon: Lutemon) {
        viewModelScope.launch {
            if (lutemon.getExperience() >= 2) {
                val randomStat = Random.nextInt(3) // 0 for HP, 1 for ATK, 2 for DEF
                var improvedStat = ""
                when (randomStat) {
                    0 -> {
                        lutemon.improveMaxHealth(1)
                        improvedStat = "HP"
                    }
                    1 -> {
                        lutemon.improveAttack(1)
                        improvedStat = "ATK"
                    }
                    2 -> {
                        lutemon.improveDefense(1)
                        improvedStat = "DEF"
                    }
                }
                lutemon.addExperience(-2)
                updateLutemonStats()
                // Update state to show the success dialog
                _uiState.update { currentState ->
                    currentState.copy(
                        showImprovementSuccessDialog = true,
                        improvementSuccessMessage = "${lutemon.name}'s $improvedStat increased by 1!"
                    )
                }
            } else {
                updateErrorMessage("XP is not enough! Need 2 XP to improve stats.")
            }
        }
    }

    // Function to clear the improvement success dialog state
    fun clearImprovementSuccessDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showImprovementSuccessDialog = false,
                improvementSuccessMessage = null
            )
        }
    }

    // Update Lutemon lists
    private fun updateLutemonLists() {
        _uiState.update { currentState ->
            currentState.copy(
                lutemonsInHome = lutemonManager.getLutemonsInLocation("Home"),
                lutemonsInTraining = lutemonManager.getLutemonsInLocation("Training"),
                lutemonsInBattle = lutemonManager.getLutemonsInLocation("Battle"),
                errorMessage = null
            )
        }
    }

    // Update Lutemon stats (called after improving stats)
    fun updateLutemonStats() {
        updateLutemonLists()
    }

    // Update error message
    fun updateErrorMessage(message: String) {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = message)
        }
    }

    // Clear error message
    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    // Clear logs
    fun clearLogs() {
        _uiState.update { currentState ->
            currentState.copy(
                battleLog = emptyList(),
                trainingLog = emptyList(),
                battleEnded = false
            )
        }
        battleSystem = null  // Reset battle system when clearing logs
    }

    // Reset battle state before starting a new battle
    fun resetBattleState() {
        _uiState.update { currentState ->
            currentState.copy(
                battleEnded = false,
                battleLog = emptyList() // Also clear the log for the new battle
            )
        }
        battleSystem = null // Ensure battle system instance is reset
    }

    // 获取当前回合是否是 Lutemon1 的回合
    fun isLutemon1Turn(): Boolean {
        return battleSystem?.isLutemon1Turn() ?: true
    }
}