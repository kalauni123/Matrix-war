package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlasmaMagenta
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidElevated
import com.example.ui.theme.VoidSurface

data class CodexTopic(
    val title: String,
    val mathFormula: String,
    val formulaColor: Color,
    val summary: String,
    val tacticalApplication: String
)

@Composable
fun CodexScreen(
    modifier: Modifier = Modifier
) {
    val topics = listOf(
        CodexTopic(
            title = "1. Matrix Algebra & Noncommutativity",
            mathFormula = "p' = A · p  |  F·R ≠ R·F",
            formulaColor = NeonCyan,
            summary = "Linear matrix operations rotate, reflect, scale, and translate coordinate points on the tactical plane. Because matrix multiplication is noncommutative, the sequence of operations fundamentally alters final battlefield geometry.",
            tacticalApplication = "Apply Spatial Shift to pivot friendly defenders behind enemy rear flanks. Rotate then reflect to reposition units without consuming movement steps."
        ),
        CodexTopic(
            title = "2. Eigenvalues & Resonances",
            mathFormula = "A v = λ v",
            formulaColor = PlasmaMagenta,
            summary = "An eigenvector v is an invariant direction that preserves its angular trajectory under a linear transformation A, scaling only by factor λ.",
            tacticalApplication = "Align attack trajectories directly along an enemy barrier's dominant eigenvector to induce harmonic resonance, resulting in 300% critical damage penetration."
        ),
        CodexTopic(
            title = "3. Determinants & Singularity Strike",
            mathFormula = "det(A) = ad - bc = 0",
            formulaColor = SingularityGold,
            summary = "The determinant measures signed 2D area distortion. When det(A) = 0, the transformation tensor collapses 2D space into a single dimensional line or point.",
            tacticalApplication = "Use Singularity Strikes to force the enemy's shield tensor to det = 0, completely disintegrating defensive energy structures for unmitigated pure damage."
        ),
        CodexTopic(
            title = "4. Vector Geometry & Specular Ricochets",
            mathFormula = "r = d - 2(d · n) n",
            formulaColor = NeonCyan,
            summary = "Projectiles travel along direction vectors d. Upon encountering reflective barriers with surface normal n, the reflected ray r preserves specular momentum.",
            tacticalApplication = "Bounce sniper laser beams off mirror barriers to bypass front-facing enemy armor and strike vulnerable targets behind obstacles."
        ),
        CodexTopic(
            title = "5. Continuous Field Diffusion (PDE)",
            mathFormula = "∂E/∂t = D ∇²E - γE + S",
            formulaColor = MatrixGreen,
            summary = "Thermal energy from flux reactors continuously diffuses across the battlefield according to discrete 5-point stencil Laplacian heat dynamics.",
            tacticalApplication = "Use Flux Siphon in high-temperature zones to convert environmental heat fields directly into unit shields and tactical energy."
        ),
        CodexTopic(
            title = "6. Inner Product Shield Mitigation",
            mathFormula = "cos θ = (v · n) / (||v|| ||n||)",
            formulaColor = SingularityGold,
            summary = "Directional shields evaluate incoming attack trajectory v against barrier surface normal n. Frontal impacts (cos θ > 0.35) absorb up to 70% of kinetic energy.",
            tacticalApplication = "Never strike shielded defenders head-on. Flank around their directional normal vector to deal full unmitigated damage."
        ),
        CodexTopic(
            title = "7. Game Theory & Minimax Equilibria",
            mathFormula = "max_i min_j A_ij = min_j max_i A_ij",
            formulaColor = PlasmaMagenta,
            summary = "In zero-sum matrix conflicts, each faction anticipates optimal opposing counter-actions to achieve a stable Nash or Minimax equilibrium.",
            tacticalApplication = "Deploy mixed spatial transformations to keep enemy AI heuristic solvers uncertain and unable to fortify predictable target zones."
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .testTag("codex_header_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TACTICAL FIELD MANUAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Mathematical Warfare Doctrine & Tensor Principles",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        items(topics) { topic ->
            Card(
                colors = CardDefaults.cardColors(containerColor = VoidCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = topic.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.mathFormula,
                        color = topic.formulaColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topic.summary,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TACTICAL APPLICATION:",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = topic.tacticalApplication,
                        color = TextPrimary.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
