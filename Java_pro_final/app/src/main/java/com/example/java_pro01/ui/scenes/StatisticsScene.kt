package com.example.java_pro01.ui.scenes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.java_pro01.viewmodels.LutemonViewModel

@Composable
fun StatisticsScene(
    viewModel: LutemonViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val allLutemons = uiState.lutemonsInHome + uiState.lutemonsInTraining + uiState.lutemonsInBattle
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (allLutemons.isEmpty()) {
            Text(
                text = "No Lutemons created yet",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allLutemons) { lutemon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${lutemon.name} (${lutemon.color})",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                                
                                // 添加图例
                                Row(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(Color(0xFF4CAF50))
                                        )
                                        Text(
                                            text = "Wins",
                                            style = TextStyle(
                                                color = Color.DarkGray,
                                                fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp)
                                            )
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(Color(0xFFE57373))
                                        )
                                        Text(
                                            text = "Losses",
                                            style = TextStyle(
                                                color = Color.DarkGray,
                                                fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp)
                                            )
                                        )
                                    }
                                }
                            }
                        
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 添加柱状图
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val wins = lutemon.getWins()
                                    val losses = lutemon.getBattleCount() - wins
                                    val maxValue = maxOf(wins, losses, 1)
                                    
                                    // 绘制坐标轴
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(50f, size.height - 50f),
                                        end = Offset(50f, 10f),
                                        strokeWidth = 1.5f
                                    )
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(50f, size.height - 50f),
                                        end = Offset(size.width - 20f, size.height - 50f),
                                        strokeWidth = 1.5f
                                    )
                                    
                                    // 计算柱子的宽度和间距
                                    val barWidth = 40f
                                    val barSpacing = (size.width - 120f) / 3
                                    
                                    // 绘制胜利柱子（绿色）
                                    val winHeight = (wins.toFloat() / maxValue) * (size.height - 70f)
                                    drawRect(
                                        color = Color(0xFF4CAF50),
                                        topLeft = Offset(50f + barSpacing, size.height - 50f - winHeight),
                                        size = Size(barWidth, winHeight)
                                    )
                                    
                                    // 绘制失败柱子（红色）
                                    val lossHeight = (losses.toFloat() / maxValue) * (size.height - 70f)
                                    drawRect(
                                        color = Color(0xFFE57373),
                                        topLeft = Offset(50f + barSpacing * 2 + barWidth, size.height - 50f - lossHeight),
                                        size = Size(barWidth, lossHeight)
                                    )
                                    
                                    // 绘制数值
                                    if (wins > 0) {
                                        drawText(
                                            textMeasurer = textMeasurer,
                                            text = wins.toString(),
                                            topLeft = Offset(50f + barSpacing + barWidth/4, size.height - 60f - winHeight),
                                            style = TextStyle(
                                                color = Color.DarkGray,
                                                fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                                            )
                                        )
                                    }
                                    if (losses > 0) {
                                        drawText(
                                            textMeasurer = textMeasurer,
                                            text = losses.toString(),
                                            topLeft = Offset(50f + barSpacing * 2 + barWidth + barWidth/4, size.height - 60f - lossHeight),
                                            style = TextStyle(
                                                color = Color.DarkGray,
                                                fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                                            )
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 显示其他统计信息
                            Text("Total Battles: ${lutemon.getBattleCount()}")
                            Text("Win Rate: ${String.format("%.1f%%", lutemon.getWinRate() * 100)}")
                            Text("Training Sessions: ${lutemon.getTrainingCount()}")
                            Text("Total Experience: ${lutemon.getExperience()}")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Return Home")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}