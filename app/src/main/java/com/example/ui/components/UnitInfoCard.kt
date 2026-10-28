package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TacticalUnit
import com.example.model.UnitFaction
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumBlue
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidCard

@Composable
fun UnitInfoCard(
    unit: TacticalUnit?,
    onDeselect: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (unit == null) return

    val isPlayer = unit.faction == UnitFaction.PLAYER
    val factionColor = if (isPlayer) NeonCyan else AlertRed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VoidCard)
            .border(1.dp, factionColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
            .testTag("unit_info_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = unit.name,
                        color = factionColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${unit.pos.x}, ${unit.pos.y})",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (unit.isShieldActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Active",
                            tint = QuantumBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${unit.unitClass.title} • ${unit.unitClass.description}",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // HP Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HP: ${unit.hp}/${unit.maxHp}",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(72.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E2638))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(unit.hpPercent)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (unit.hpPercent > 0.45f) MatrixGreen else AlertRed)
                        )
                    }
                }
            }

            // Deselect button
            IconButton(
                onClick = onDeselect,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("deselect_unit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
