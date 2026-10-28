package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun SkirmishSetupScreen(
    onLaunchSkirmish: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGridSize by remember { mutableIntStateOf(10) }
    var selectedDifficulty by remember { mutableStateOf("Minimax Overseer") }
    var hazardDensity by remember { mutableStateOf("Moderate") }
    var pdeIntensity by remember { mutableStateOf("Active Diffusion") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .testTag("skirmish_header_card")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TACTICAL SKIRMISH GENERATOR",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Custom battlefield simulation & AI parameters",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Grid Dimensions Section
        OptionGroup(
            title = "BATTLEFIELD GRID DIMENSIONS",
            options = listOf("8 × 8 Blitz", "10 × 10 Tactical"),
            selected = if (selectedGridSize == 8) "8 × 8 Blitz" else "10 × 10 Tactical",
            onSelect = { selectedGridSize = if (it == "8 × 8 Blitz") 8 else 10 }
        )

        // AI Heuristic Difficulty
        OptionGroup(
            title = "ENEMY AI STRATEGY ENGINE",
            options = listOf("Standard Swarm", "Geometric Flanker", "Minimax Overseer"),
            selected = selectedDifficulty,
            onSelect = { selectedDifficulty = it }
        )

        // Hazard Zones
        OptionGroup(
            title = "ENTROPY HAZARDS & VOID CELLS",
            options = listOf("Minimal", "Moderate", "Extreme Void"),
            selected = hazardDensity,
            onSelect = { hazardDensity = it }
        )

        // PDE Flux Reactor Intensity
        OptionGroup(
            title = "PDE REACTOR THERMAL DIFFUSION",
            options = listOf("Sub-Critical", "Active Diffusion", "Thermal Storm"),
            selected = pdeIntensity,
            onSelect = { pdeIntensity = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { onLaunchSkirmish(1) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("launch_skirmish_button")
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = VoidBlack,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "INITIATE SIMULATION",
                color = VoidBlack,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun OptionGroup(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VoidCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { option ->
                    val isSelected = option == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else VoidSurface)
                            .border(1.dp, if (isSelected) NeonCyan else GridLineColor, RoundedCornerShape(6.dp))
                            .clickable { onSelect(option) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) NeonCyan else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
