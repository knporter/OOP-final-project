package com.example.java_pro01.training

import com.example.java_pro01.models.Lutemon

/**
 * Training System - Handles Lutemon training and attribute enhancement
 */
class TrainingSystem {
    // Training records
    private val trainingLog = mutableListOf<String>()

    // Execute training
    fun train(lutemon: Lutemon): TrainingResult {
        trainingLog.clear()
        logTraining("Starting training: ${lutemon.name}")
        logTraining("Status before training: ${lutemon}")

        // 记录训练次数
        lutemon.recordTraining()
        
        // Gain 1 experience point from training
        lutemon.addExperience(1)

        logTraining("Training complete!")
        logTraining("Status after training: ${lutemon}")

        return TrainingResult(lutemon, trainingLog.toList())
    }

    // Record training log
    private fun logTraining(message: String) {
        trainingLog.add(message)
    }
}

/**
 * Training result data class
 */
data class TrainingResult(
    val trainedLutemon: Lutemon,
    val trainingLog: List<String>
)