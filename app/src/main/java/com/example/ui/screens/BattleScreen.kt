package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BattlePhase
import com.example.model.GridPos
import com.example.ui.components.AbilityBar
import com.example.ui.components.MathHudOverlay
import com.example.ui.components.TacticalGridView
import com.example.ui.components.UnitInfoCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidElevated
import com.example.ui.theme.VoidSurface
import com.example.viewmodel.BattleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.battleState.collectAsStateWithLifecycle()
    var showLogsModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.sectorTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Text(
                            text = "Turn ${state.turnNumber} • ${if (state.phase == BattlePhase.PLAYER_TURN) "PLAYER PHASE" else "AI CALCULATING"}",
                            fontSize = 11.sp,
                            color = if (state.phase == BattlePhase.PLAYER_TURN) MatrixGreen else AlertRed,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("battle_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retreat to Base",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogsModal = !showLogsModal },
                        modifier = Modifier.testTag("toggle_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Combat Logs",
                            tint = NeonCyan
                        )
                    }
                    IconButton(
                        onClick = { viewModel.initializeSector(state.sectorId) },
                        modifier = Modifier.testTag("restart_battle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Restart Battle",
                            tint = TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VoidSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = VoidBlack,
        modifier = modifier.testTag("battle_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Math HUD Overlay
            MathHudOverlay(
                state = state,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Tactical 10x10 Grid View
            TacticalGridView(
                state = state,
                onCellClick = { pos -> viewModel.onCellClicked(pos) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Selected Unit Info Card (if unit selected)
            if (state.selectedUnit != null) {
                UnitInfoCard(
                    unit = state.selectedUnit,
                    onDeselect = { viewModel.deselectUnit() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 4. Ability Tray & Controls
            AbilityBar(
                state = state,
                onSelectAbility = { viewModel.onSelectAbility(it) },
                onActivateShield = { viewModel.onActivateShield() },
                onEndTurn = { viewModel.onEndTurn() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Victory Dialog
    if (state.phase == BattlePhase.VICTORY) {
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MatrixGreen, RoundedCornerShape(16.dp))
                    .padding(4.dp)
                    .testTag("victory_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = SingularityGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "VICTORY ACHIEVED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatrixGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Sector Cleared • Mathematical Objective Complete",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SingularityGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoidSurface)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Singularity Collapses: ${state.statsSingularitiesTriggered}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Eigen Resonances: ${state.statsEigenHits}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Total Pure Damage: ${state.statsDamageDealt}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Reward: +475 Credits • +3 Cores",
                            color = SingularityGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("victory_continue_button")
                    ) {
                        Text(
                            text = "CONTINUE TO CAMPAIGN",
                            color = VoidBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Defeat Dialog
    if (state.phase == BattlePhase.DEFEAT) {
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, AlertRed, RoundedCornerShape(16.dp))
                    .padding(4.dp)
                    .testTag("defeat_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FLEET COLLAPSED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enemy AI broke your defensive tensors. Reconfigure your mathematical operations and retry.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.initializeSector(state.sectorId) },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("retry_battle_button")
                    ) {
                        Text(
                            text = "RETRY SECTOR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = VoidSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "RETREAT TO HEADQUARTERS",
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }

    // Combat Logs Dialog
    if (showLogsModal) {
        Dialog(onDismissRequest = { showLogsModal = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TACTICAL TELEMETRY LOG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(onClick = { showLogsModal = false }) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(state.combatLogs) { log ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VoidSurface)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Turn ${log.turn}",
                                            fontSize = 9.sp,
                                            color = TextMuted,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (log.formula != null) {
                                            Text(
                                                text = log.formula,
                                                fontSize = 9.sp,
                                                color = SingularityGold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                    Text(
                                        text = log.message,
                                        fontSize = 11.sp,
                                        color = if (log.isCrit) MatrixGreen else if (log.isPlayerAction) TextPrimary else AlertRed,
                                        fontWeight = if (log.isCrit) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
