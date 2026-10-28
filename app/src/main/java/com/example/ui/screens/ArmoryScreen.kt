package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GameRepository
import com.example.data.entity.TechUpgradeEntity
import com.example.model.UnitClass
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
import kotlinx.coroutines.launch

@Composable
fun ArmoryScreen(
    repository: GameRepository,
    modifier: Modifier = Modifier
) {
    val profile by repository.playerProfile.collectAsStateWithLifecycle(initialValue = null)
    val upgrades by repository.allTechUpgrades.collectAsStateWithLifecycle(initialValue = emptyList())
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Player Wealth Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, SingularityGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .testTag("armory_header_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMMAND OVERCLOCK ARSENAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SingularityGold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = profile?.rankTitle ?: "Vector Cadet",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VoidSurface)
                            .border(1.dp, SingularityGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${profile?.credits ?: 0} ₢",
                            color = SingularityGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "MATHEMATICAL ALGORITHM RESEARCH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // Tech Upgrades List
        items(upgrades) { tech ->
            TechCard(
                tech = tech,
                canAfford = (profile?.credits ?: 0) >= tech.upgradeCost && !tech.isMaxed,
                onUpgrade = {
                    coroutineScope.launch {
                        repository.purchaseUpgrade(tech.techId)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "TACTICAL CLASS SQUAD PROFILES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // Unit Class Showcase
        items(UnitClass.values()) { unitClass ->
            UnitClassCard(unitClass)
        }
    }
}

@Composable
private fun TechCard(
    tech: TechUpgradeEntity,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VoidCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (tech.currentLevel > 0) NeonCyan.copy(alpha = 0.5f) else GridLineColor, RoundedCornerShape(10.dp))
            .testTag("tech_card_${tech.techId}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tech.name,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Formula: ${tech.mathFormula}",
                        color = SingularityGold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Level Dots
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 0 until tech.maxLevel) {
                        val isFilled = i < tech.currentLevel
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isFilled) NeonCyan else GridLineColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tech.description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (tech.isMaxed) "MAX LEVEL" else "Cost: ${tech.upgradeCost} ₢",
                    color = if (tech.isMaxed) MatrixGreen else SingularityGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        disabledContainerColor = VoidSurface
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("upgrade_btn_${tech.techId}")
                ) {
                    if (tech.isMaxed) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MatrixGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MAX", color = MatrixGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.Upgrade, contentDescription = null, tint = if (canAfford) VoidBlack else TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RESEARCH", color = if (canAfford) VoidBlack else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitClassCard(unitClass: UnitClass) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VoidSurface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GridLineColor, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = unitClass.title, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = unitClass.description, color = TextSecondary, fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "HP: ${unitClass.baseHp}", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(text = "ATK: ${unitClass.baseDamage} (R:${unitClass.attackRange})", color = SingularityGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text(text = "MOV: ${unitClass.moveRange}", color = MatrixGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
