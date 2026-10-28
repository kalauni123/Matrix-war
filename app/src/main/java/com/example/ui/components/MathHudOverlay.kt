package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.BattleState
import com.example.model.Matrix2x2
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlasmaMagenta
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidElevated
import com.example.ui.theme.VoidSurface

@Composable
fun MathHudOverlay(
    state: BattleState,
    modifier: Modifier = Modifier
) {
    val activeMatrix = state.selectedAbility?.matrixRep ?: Matrix2x2.IDENTITY
    val detVal = activeMatrix.det
    val isSingular = activeMatrix.isSingular

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VoidSurface.copy(alpha = 0.95f))
            .border(1.dp, GridLineColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("math_hud_overlay")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Matrix 2x2 Bracket Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "A =",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(VoidElevated)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "[ ${formatFloat(activeMatrix.a)}  ${formatFloat(activeMatrix.b)} ]",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "[ ${formatFloat(activeMatrix.c)}  ${formatFloat(activeMatrix.d)} ]",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 2. Determinant status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "det(A)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (isSingular) "0 (SINGULAR)" else formatFloat(detVal),
                    color = if (isSingular) SingularityGold else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // 3. Eigen Resonance / PDE Indicator
            Column(horizontalAlignment = Alignment.End) {
                if (state.eigenBarrier != null) {
                    Text(
                        text = "λ-Barrier",
                        color = PlasmaMagenta,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${state.eigenBarrier.resonantAngleDeg.toInt()}° Resonant",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "PDE Flux",
                        color = MatrixGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    val avgHeat = state.cells.values.map { it.diffusionEnergy }.average().toFloat()
                    Text(
                        text = "∇²E: ${(avgHeat).toInt()} J",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun formatFloat(v: Float): String {
    return if (v == v.toInt().toFloat()) "${v.toInt()}" else String.format("%.1f", v)
}
