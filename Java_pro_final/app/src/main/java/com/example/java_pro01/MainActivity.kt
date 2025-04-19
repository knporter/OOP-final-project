package com.example.java_pro01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.java_pro01.ui.theme.Java_pro01Theme
import com.example.java_pro01.viewmodels.LutemonViewModel
import com.example.java_pro01.viewmodels.LutemonUiState
import com.example.java_pro01.models.Lutemon
import com.example.java_pro01.models.LutemonStatsProvider
import com.example.java_pro01.ui.scenes.HomeScene
import com.example.java_pro01.ui.scenes.TrainingScene
import com.example.java_pro01.ui.scenes.BattleScene
import com.example.java_pro01.ui.scenes.StatisticsScene

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Java_pro01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LutemonApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LutemonApp(viewModel: LutemonViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedScene by remember { mutableStateOf("Home") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Lutemon") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SceneButton("Home", selectedScene) { selectedScene = "Home" }
            SceneButton("Training", selectedScene) { selectedScene = "Training" }
            SceneButton("Battle", selectedScene) { selectedScene = "Battle" }
            SceneButton("Stats", selectedScene) { selectedScene = "Stats" }
        }

        when (selectedScene) {
            "Home" -> HomeScene(
                viewModel = viewModel,
                onNavigateToTraining = { selectedScene = "Training" },
                onNavigateToBattle = { selectedScene = "Battle" }
            )
            "Training" -> TrainingScene(
                viewModel = viewModel,
                onNavigateBack = { selectedScene = "Home" }
            )
            "Battle" -> BattleScene(
                viewModel = viewModel,
                onNavigateBack = { selectedScene = "Home" }
            )
            "Stats" -> StatisticsScene(
                viewModel = viewModel,
                onNavigateBack = { selectedScene = "Home" }
            )
        }
    }

    uiState.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SceneButton(scene: String, selectedScene: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (scene == selectedScene) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Text(scene)
    }
}