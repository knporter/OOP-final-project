package com.example.java_pro01.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.java_pro01.viewmodels.LutemonViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun TrainingScene(
    viewModel: LutemonViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lutemonsInTraining = uiState.lutemonsInTraining
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Training Area",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (lutemonsInTraining.isEmpty()) {
            Text(
                text = "No Lutemons in training",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lutemonsInTraining) { lutemon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Name: ${lutemon.name}")
                            Text("Color: ${lutemon.color}")
                            Text("HP: ${lutemon.currentHealth}/${lutemon.getMaxHealth()}")
                            Text("ATK: ${lutemon.getAttack()}")
                            Text("DEF: ${lutemon.getDefense()}")
                            Text("XP: ${lutemon.getExperience()}")
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                var isTraining by remember { mutableStateOf(false) }
                                var progress by remember { mutableStateOf(0f) }
                                
                                Column {
                                    if (isTraining) {
                                        LinearProgressIndicator(
                                            progress = progress,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                        )
                                    }
                                    
                                    Button(
                                        onClick = {
                                            if (!isTraining) {
                                                isTraining = true
                                                progress = 0f
                                                
                                                scope.launch {
                                                    // 在3秒内更新进度条
                                                    repeat(30) {
                                                        delay(100)
                                                        progress = (it + 1) / 30f
                                                    }
                                                    // 训练完成后更新状态
                                                    viewModel.startTraining(lutemon)
                                                    isTraining = false
                                                    progress = 0f
                                                }
                                            }
                                        },
                                        enabled = !isTraining
                                    ) {
                                        Text(if (isTraining) "Training..." else "Train")
                                    }
                                }
                                
                                Button(
                                    onClick = { viewModel.moveLutemon(lutemon, "Home") }
                                ) {
                                    Text("Send Home")
                                }
                            }
                        }
                    }
                }
            }
            
            // Training Log
            if (uiState.trainingLog.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Training Log",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        uiState.trainingLog.forEach { log ->
                            Text(text = log)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                viewModel.clearLogs()
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Return Home")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}