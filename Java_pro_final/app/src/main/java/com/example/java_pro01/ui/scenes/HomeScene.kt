package com.example.java_pro01.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.example.java_pro01.viewmodels.LutemonViewModel
import com.example.java_pro01.models.LutemonStatsProvider
import com.example.java_pro01.models.Lutemon
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.java_pro01.R
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScene(
    viewModel: LutemonViewModel,
    onNavigateToTraining: () -> Unit,
    onNavigateToBattle: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showStatsDialog by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf("") }
    var lutemonName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val colors = listOf("White", "Green", "Black", "Pink", "Orange")
    var showImproveDialog by remember { mutableStateOf(false) }
    var selectedLutemon: Lutemon? by remember { mutableStateOf(null) }

    // 获取图片资源ID的函数
    fun getLutemonImageResource(color: String): Int {
        return when (color.lowercase()) {
            "white" -> R.drawable.white_lutemon
            "green" -> R.drawable.green_lutemon
            "black" -> R.drawable.black_lutemon
            "pink" -> R.drawable.pink_lutemon
            "orange" -> R.drawable.orange_lutemon
            else -> R.drawable.white_lutemon // 默认图片
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lutemon Home",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // 修改按钮部分
        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create your Lutemon here")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Lutemons in a scrollable list
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.uiState.value.lutemonsInHome) { lutemon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 添加图片
                        Image(
                            painter = painterResource(getLutemonImageResource(lutemon.color)),
                            contentDescription = "${lutemon.color} Lutemon",
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Name: ${lutemon.name}")
                        Text("Color: ${lutemon.color}")
                        Text("HP: ${lutemon.currentHealth}/${lutemon.getMaxHealth()}")
                        Text("ATK: ${lutemon.getAttack()}")
                        Text("DEF: ${lutemon.getDefense()}")
                        Text("XP: ${lutemon.getExperience()}")
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { 
                                    viewModel.moveLutemon(lutemon, "Training")
                                    onNavigateToTraining()
                                },
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            ) {
                                Text("Go to Training")
                            }
                            
                            Button(
                                onClick = { 
                                    viewModel.moveLutemon(lutemon, "Battle")
                                    onNavigateToBattle()
                                },
                                modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
                            ) {
                                Text("Go to Battle")
                            }

                            Button(
                                onClick = { 
                                    if (lutemon.getExperience() >= 2) {
                                        showImproveDialog = true
                                        selectedLutemon = lutemon
                                    } else {
                                        viewModel.updateErrorMessage("XP is not enough! Need 2 XP to improve stats.")
                                    }
                                },
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            ) {
                                Text("Improve")
                            }
                        }
                    }
                }
            }
        }
    }

    // 修改创建对话框
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Choose Your Lutemon") },
            text = {
                Column {
                    // 确保说明文字在标题下方
                    Text(text = "All attributes of Lutemon is a reference. The actual damage value in the arena will be random.", style = MaterialTheme.typography.bodySmall)
                    
                    LazyColumn {
                        items(LutemonStatsProvider.lutemonBaseStats.values.toList()) { stats ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedColor = stats.color
                                        expanded = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 添加图片
                                    Image(
                                        painter = painterResource(getLutemonImageResource(stats.color)),
                                        contentDescription = "${stats.color} Lutemon",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .padding(end = 16.dp)
                                    )
                                    
                                    Column {
                                        Text("Color: ${stats.color}")
                                        Text("HP: ${stats.maxHealth}")
                                        Text("ATK: ${stats.attack}")
                                        Text("DEF: ${stats.defense}")
                                    }
                                }
                            }
                        }
                    }
                    
                    if (expanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = lutemonName,
                            onValueChange = { lutemonName = it },
                            label = { Text("Enter Lutemon Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (lutemonName.isNotBlank() && selectedColor.isNotBlank()) {
                            viewModel.createLutemon(lutemonName, selectedColor)
                            showCreateDialog = false
                            lutemonName = ""
                            selectedColor = ""
                            expanded = false
                        }
                    },
                    enabled = lutemonName.isNotBlank() && selectedColor.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { 
                    showCreateDialog = false
                    lutemonName = ""
                    selectedColor = ""
                    expanded = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStatsDialog) {
        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = { Text("Lutemon Base Stats") },
            text = {
                LazyColumn {
                    items(LutemonStatsProvider.lutemonBaseStats.values.toList()) { stats ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Color: ${stats.color}")
                                Text("HP: ${stats.maxHealth}")
                                Text("ATK: ${stats.attack}")
                                Text("DEF: ${stats.defense}")
                                Text("XP: ${stats.experience}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showStatsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Dialog for random stat improvement
    if (showImproveDialog && selectedLutemon != null) {
        val lutemon = selectedLutemon!!
        AlertDialog(
            onDismissRequest = {
                showImproveDialog = false
                selectedLutemon = null
            },
            title = { Text("Improve Stats Randomly") },
            text = {
                Text("Are you sure you want to spend 2 XP points to randomly exchange for 1 point of HP or 1 point of ATK or 1 point of DEF?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.improveRandomStat(lutemon)
                        showImproveDialog = false
                        selectedLutemon = null
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showImproveDialog = false
                        selectedLutemon = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Show error message if any
    val errorMessage = viewModel.uiState.value.errorMessage
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            // This will show a Snackbar or Toast with the error message
            // For simplicity, let's just print it for now, you might need a SnackbarHostState
            println("Error: $errorMessage")
            viewModel.clearError()
        }
    }

    // Show improvement success dialog
    if (viewModel.uiState.value.showImprovementSuccessDialog && viewModel.uiState.value.improvementSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearImprovementSuccessDialog() }, // Dismiss doesn't clear automatically
            title = { Text("Improvement Successful") },
            text = { Text(viewModel.uiState.value.improvementSuccessMessage!!) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearImprovementSuccessDialog()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}