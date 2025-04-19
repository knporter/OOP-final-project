package com.example.java_pro01.viewmodels

import com.example.java_pro01.models.Lutemon

data class LutemonUiState(
    val lutemonsInHome: List<Lutemon> = emptyList(),
    val lutemonsInTraining: List<Lutemon> = emptyList(),
    val lutemonsInBattle: List<Lutemon> = emptyList(),
    val trainingLog: List<String> = emptyList(),
    val battleLog: List<String> = emptyList(),
    val errorMessage: String? = null,
    val battleEnded: Boolean = false,
    val showImprovementSuccessDialog: Boolean = false,
    val improvementSuccessMessage: String? = null
)