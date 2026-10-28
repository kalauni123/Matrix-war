package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.screens.ArmoryScreen
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.CampaignScreen
import com.example.ui.screens.CodexScreen
import com.example.ui.screens.MatrixLabScreen
import com.example.ui.screens.SkirmishSetupScreen
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MatrixWarsTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VoidSurface
import com.example.viewmodel.BattleViewModel
import kotlinx.coroutines.launch

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    CAMPAIGN("Campaign", Icons.Default.GridOn),
    BATTLE("Battle", Icons.Default.SportsEsports),
    LAB("Academy", Icons.Default.Science),
    ARMORY("Armory", Icons.Default.Build),
    CODEX("Codex", Icons.Default.AutoStories)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = GameRepository(database.matrixWarsDao())

        setContent {
            MatrixWarsTheme {
                MatrixWarsApp(repository = repository)
            }
        }
    }
}

@Composable
fun MatrixWarsApp(repository: GameRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(AppNavDestination.CAMPAIGN) }

    LaunchedEffect(Unit) {
        repository.initializeDefaultsIfNeeded()
    }

    val battleViewModel: BattleViewModel = viewModel {
        BattleViewModel(repository = repository, context = context)
    }

    // Custom back navigation handling
    BackHandler(enabled = currentScreen != AppNavDestination.CAMPAIGN) {
        currentScreen = AppNavDestination.CAMPAIGN
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = VoidSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                AppNavDestination.values().forEach { destination ->
                    val isSelected = currentScreen == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = VoidBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppNavDestination.CAMPAIGN -> {
                    CampaignScreen(
                        repository = repository,
                        onStartSector = { sectorNum ->
                            battleViewModel.initializeSector(sectorNum)
                            currentScreen = AppNavDestination.BATTLE
                        }
                    )
                }
                AppNavDestination.BATTLE -> {
                    BattleScreen(
                        viewModel = battleViewModel,
                        onNavigateBack = { currentScreen = AppNavDestination.CAMPAIGN }
                    )
                }
                AppNavDestination.LAB -> {
                    MatrixLabScreen(
                        onRewardEarned = { credits ->
                            coroutineScope.launch {
                                // Add bonus credits reward
                                val profile = repository.playerProfile
                            }
                        }
                    )
                }
                AppNavDestination.ARMORY -> {
                    ArmoryScreen(repository = repository)
                }
                AppNavDestination.CODEX -> {
                    CodexScreen()
                }
            }
        }
    }
}
