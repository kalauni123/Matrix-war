package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BlurCircular
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AbilityType
import com.example.model.BattleState
import com.example.model.TacticalAbility
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlasmaMagenta
import com.example.ui.theme.QuantumBlue
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidElevated
import com.example.ui.theme.VoidSurface

@Composable
fun AbilityBar(
    state: BattleState,
    onSelectAbility: (TacticalAbility) -> Unit,
    onActivateShield: () -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ability_bar")
    ) {
        // Resource Indicators (Energy & AP)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Points (AP)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AP: ",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                for (i in 0 until state.maxActionPoints) {
                    val isActive = i < state.playerActionPoints
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isActive) MatrixGreen else GridLineColor)
                    )
                }
            }

            // Energy Core Reserve
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${state.playerEnergy} / ${state.maxPlayerEnergy} E",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // End Turn Button
            Button(
                onClick = onEndTurn,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("end_turn_button")
            ) {
                Text(
                    text = "END TURN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal Ability Scroll Tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Directional Shield toggle
            val isShieldActive = state.selectedUnit?.isShieldActive == true
            AbilityCard(
                title = "Tensor Shield",
                formula = "v·n > 0",
                cost = "15 E",
                icon = Icons.Default.Shield,
                color = QuantumBlue,
                isSelected = isShieldActive,
                isEnabled = state.selectedUnit != null && state.playerEnergy >= 15,
                onClick = onActivateShield,
                testTag = "shield_ability_btn"
            )

            // Dynamic Mathematical Abilities
            for (ability in TacticalAbility.ALL_ABILITIES) {
                val isSelected = state.selectedAbility?.type == ability.type
                val canAfford = state.playerEnergy >= ability.energyCost &&
                        state.playerActionPoints >= ability.actionPointCost

                val color = when (ability.type) {
                    AbilityType.SPATIAL_SHIFT -> NeonCyan
                    AbilityType.MIRROR_STRIKE -> NeonCyan
                    AbilityType.VECTOR_DASH -> MatrixGreen
                    AbilityType.SINGULARITY_STRIKE -> SingularityGold
                    AbilityType.EIGEN_WEAPON -> PlasmaMagenta
                    AbilityType.RICOCHET_BEAM -> Color(0xFFC0D8F0)
                    AbilityType.FLUX_SIPHON -> MatrixGreen
                    else -> NeonCyan
                }

                val icon = when (ability.type) {
                    AbilityType.SPATIAL_SHIFT -> Icons.Default.RotateRight
                    AbilityType.MIRROR_STRIKE -> Icons.Default.Flip
                    AbilityType.VECTOR_DASH -> Icons.Default.PlayArrow
                    AbilityType.SINGULARITY_STRIKE -> Icons.Default.BlurCircular
                    AbilityType.EIGEN_WEAPON -> Icons.Default.FlashOn
                    AbilityType.RICOCHET_BEAM -> Icons.Default.Timeline
                    AbilityType.FLUX_SIPHON -> Icons.Default.Waves
                    else -> Icons.Default.Autorenew
                }

                AbilityCard(
                    title = ability.name,
                    formula = ability.formulaSymbol,
                    cost = "${ability.energyCost}E (${ability.actionPointCost}AP)",
                    icon = icon,
                    color = color,
                    isSelected = isSelected,
                    isEnabled = canAfford,
                    onClick = { onSelectAbility(ability) },
                    testTag = "ability_${ability.type.name.lowercase()}"
                )
            }
        }
    }
}

@Composable
private fun AbilityCard(
    title: String,
    formula: String,
    cost: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor = if (isSelected) color else if (isEnabled) GridLineColor else GridLineColor.copy(alpha = 0.3f)
    val bgColor = if (isSelected) color.copy(alpha = 0.22f) else VoidCard

    Box(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(6.dp)
            .testTag(testTag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isEnabled) color else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = if (isEnabled) TextPrimary else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = formula,
                color = color.copy(alpha = if (isEnabled) 0.9f else 0.4f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cost,
                color = if (isEnabled) TextSecondary else TextMuted.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
