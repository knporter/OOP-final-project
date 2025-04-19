package com.example.java_pro01.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.example.java_pro01.viewmodels.LutemonViewModel
import com.example.java_pro01.models.Lutemon
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun BattleScene(
    viewModel: LutemonViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lutemonsInBattle = uiState.lutemonsInBattle
    val scope = rememberCoroutineScope()

    // 添加状态变量
    var battleWinner by remember { mutableStateOf<Lutemon?>(null) }
    var showBattleEndDialog by remember { mutableStateOf(false) }

    // 添加动画状态
    var isAttacking by remember { mutableStateOf(false) }
    var isHealing by remember { mutableStateOf(false) }
    var isBattleInProgress by remember { mutableStateOf(false) }
    val isBattlePaused = remember { mutableStateOf(false) }
    
    // 攻击动画
    val attackOffset by animateFloatAsState(
        targetValue = if (isAttacking) 100f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { isAttacking = false }
    )
    
    // 治疗动画
    val healOffset by animateFloatAsState(
        targetValue = if (isHealing) -30f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isHealing = false }
    )

    // 自动战斗函数
    fun autoBattle(lutemon1: Lutemon, lutemon2: Lutemon) {
        scope.launch {
            isBattleInProgress = true
            var turnCount = 1 // 添加回合计数器
            
            while (!uiState.battleEnded) {
                // 检查生命值并立即显示弹窗
                if (lutemon1.currentHealth <= 0 || lutemon2.currentHealth <= 0) {
                    battleWinner = when {
                        lutemon1.currentHealth <= 0 -> lutemon2
                        lutemon2.currentHealth <= 0 -> lutemon1
                        else -> null
                    }
                    showBattleEndDialog = true
                    isBattleInProgress = false
                    break
                }
                if (isBattlePaused.value) {
                    delay(100) // 暂停时短暂等待
                    continue
                }
                // 前两回合强制攻击，从第三回合开始才有治疗的可能
                val action = if (turnCount <= 2) {
                    "attack"
                } else {
                    if (Random.nextFloat() < 0.3f) "heal" else "attack"
                }
                
                // 执行动作并等待动画完成
                if (action == "attack") {
                    isAttacking = true
                } else {
                    isHealing = true
                }
                
                viewModel.performBattleAction(lutemon1, lutemon2, action)
                
                // 等待动画完成
                delay(600) // 动画时间
                
                // 额外等待时间，确保每回合之间有明显间隔
                delay(1250) // 回合间隔时间改为1.25秒
                
                turnCount++ // 增加回合计数
            }
            isBattleInProgress = false
            
            // 如果双方都还活着，根据血量决定胜者
            if (battleWinner == null) {
                battleWinner = if (lutemon1.currentHealth > lutemon2.currentHealth) lutemon1 else lutemon2
                showBattleEndDialog = true
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Battle Arena",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        if (lutemonsInBattle.size >= 2) {
            val lutemon1 = lutemonsInBattle[0]
            val lutemon2 = lutemonsInBattle[1]

            // 显示对战双方信息
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Lutemon 1信息
                

    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp)
                                .offset(
                                    x = if (viewModel.isLutemon1Turn()) attackOffset.dp else 0.dp,
                                    y = if (viewModel.isLutemon1Turn()) healOffset.dp else 0.dp
                                )
                        ) {
                            // 绘制圆形脸
                            drawCircle(
                                color = when (lutemon1.color.lowercase()) {
                                    "white" -> Color(0xFFFFFFFF)  // 纯白色
                                    "green" -> Color(0xFF66BB6A)  // 浅绿色
                                    "black" -> Color(0xFF212121)  // 纯黑色
                                    "pink" -> Color(0xFFF48FB1)   // 浅粉色
                                    "orange" -> Color(0xFFFFB74D) // 浅橙色
                                    else -> Color.White
                                },
                                radius = size.minDimension / 2
                            )
                            
                            // 绘制眼睛
                            val eyeRadius = size.minDimension / 10
                            drawCircle(
                                color = Color.Black,
                                radius = eyeRadius,
                                center = Offset(size.width * 0.35f, size.height * 0.4f)
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = eyeRadius,
                                center = Offset(size.width * 0.65f, size.height * 0.4f)
                            )
                            
                            // 绘制微笑
                            val smilePath = Path().apply {
                                moveTo(size.width * 0.3f, size.height * 0.6f)
                                quadraticBezierTo(
                                    size.width * 0.5f, size.height * 0.8f,
                                    size.width * 0.7f, size.height * 0.6f
                                )
                            }
                            drawPath(
                                path = smilePath,
                                color = Color.Black,
                                style = Stroke(width = size.minDimension / 20)
                            )
                        }
                        Text(text = lutemon1.name)
                        Text(
                            text = "HP: ${lutemon1.currentHealth}/${lutemon1.getMaxHealth()}",
                            color = if (lutemon1.currentHealth < lutemon1.getMaxHealth() * 0.3f) 
                                Color.Red 
                            else 
                                Color.Black
                        )
                    }

                    Text(
                        text = "VS",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Lutemon 2信息
                

    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp)
                                .offset(
                                    x = if (!viewModel.isLutemon1Turn()) -attackOffset.dp else 0.dp,
                                    y = if (!viewModel.isLutemon1Turn()) healOffset.dp else 0.dp
                                )
                        ) {
                            // 绘制圆形脸
                            drawCircle(
                                color = when (lutemon2.color.lowercase()) {
                                    "white" -> Color(0xFFFFFFFF)  // 纯白色
                                    "green" -> Color(0xFF66BB6A)  // 浅绿色
                                    "black" -> Color(0xFF212121)  // 纯黑色
                                    "pink" -> Color(0xFFF48FB1)   // 浅粉色
                                    "orange" -> Color(0xFFFFB74D) // 浅橙色
                                    else -> Color.White
                                },
                                radius = size.minDimension / 2
                            )
                            
                            // 绘制眼睛
                            val eyeRadius = size.minDimension / 10
                            drawCircle(
                                color = Color.Black,
                                radius = eyeRadius,
                                center = Offset(size.width * 0.35f, size.height * 0.4f)
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = eyeRadius,
                                center = Offset(size.width * 0.65f, size.height * 0.4f)
                            )
                            
                            // 绘制微笑
                            val smilePath = Path().apply {
                                moveTo(size.width * 0.3f, size.height * 0.6f)
                                quadraticBezierTo(
                                    size.width * 0.5f, size.height * 0.8f,
                                    size.width * 0.7f, size.height * 0.6f
                                )
                            }
                            drawPath(
                                path = smilePath,
                                color = Color.Black,
                                style = Stroke(width = size.minDimension / 20)
                            )
                        }
                        Text(text = lutemon2.name)
                        Text(
                            text = "HP: ${lutemon2.currentHealth}/${lutemon2.getMaxHealth()}",
                            color = if (lutemon2.currentHealth < lutemon2.getMaxHealth() * 0.3f) 
                                Color.Red 
                            else 
                                Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 按钮逻辑
            if (!isBattleInProgress || isBattlePaused.value) {
                Button(
                    onClick = {
                        if (isBattlePaused.value) {
                            isBattlePaused.value = false
                            isBattleInProgress = true
                        } else {
                            // Reset battle state before starting a new battle
                            viewModel.resetBattleState()
                            autoBattle(lutemon1, lutemon2)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    if (isBattlePaused.value) {
                        Text("Continue")
                    } else {
                        Text("Begin Battle")
                    }
                }

                Button(
                    onClick = {
                        // 返回家并恢复血量
                        viewModel.moveLutemon(lutemon1, "Home")
                        viewModel.moveLutemon(lutemon2, "Home")
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Return Home")
                }
            } else {
                Button(
                    onClick = {
                        isBattlePaused.value = true
                        isBattleInProgress = false
                    },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text("Stop Battle")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 战斗日志
            if (uiState.battleLog.isNotEmpty()) {
                val listState = rememberLazyListState()
                LaunchedEffect(uiState.battleLog) {
                    listState.animateScrollToItem(uiState.battleLog.size - 1)
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Battle Log",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.battleLog) { log ->
                            Text(text = log)
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Please select two Lutemons for battle",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }

    // 添加战斗结束对话框
    if (showBattleEndDialog) {
        AlertDialog(
            onDismissRequest = { showBattleEndDialog = false },
            title = { Text("Battle is over") },
            confirmButton = {
                Button(
                    onClick = {
                        // 先将两个Lutemon送回Home
                        lutemonsInBattle.forEach { lutemon ->
                            viewModel.moveLutemon(lutemon, "Home")
                        }
                        // 然后关闭对话框
                        showBattleEndDialog = false
                        // 最后返回主页面
                        onNavigateBack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}