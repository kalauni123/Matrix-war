package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GridPos
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
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidElevated
import com.example.ui.theme.VoidSurface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MatrixLabScreen(
    onRewardEarned: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Transformations", "Determinants", "Eigenvalues", "PDE Diffusion")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(14.dp)
            .testTag("matrix_lab_screen")
    ) {
        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MATRIX ACADEMY & LAB",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Interactive mathematical sandbox & tactical simulations",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = VoidSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> MatrixTransformationsLab()
                1 -> DeterminantsLab(onRewardEarned)
                2 -> EigenvaluesLab()
                3 -> PdeDiffusionLab()
            }
        }
    }
}

@Composable
private fun MatrixTransformationsLab() {
    var vectorX by remember { mutableFloatStateOf(2f) }
    var vectorY by remember { mutableFloatStateOf(3f) }
    var activeMatrix by remember { mutableStateOf(Matrix2x2.ROTATION_90) }
    var matrixName by remember { mutableStateOf("Rotation 90° Clockwise") }

    val transformedX = activeMatrix.a * vectorX + activeMatrix.b * vectorY
    val transformedY = activeMatrix.c * vectorX + activeMatrix.d * vectorY

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Concept Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Linear Vector Transformations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "A matrix represents a geometric transformation that maps input coordinate vector p to p' = A·p. Notice how rotation and reflection maintain vector magnitude while altering tactical orientation.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Visual Coordinate Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VoidSurface)
                .border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val scale = 22f

                // Axes
                drawLine(color = GridLineColor, start = Offset(0f, cy), end = Offset(size.width, cy), strokeWidth = 1.dp.toPx())
                drawLine(color = GridLineColor, start = Offset(cx, 0f), end = Offset(cx, size.height), strokeWidth = 1.dp.toPx())

                // Original Vector (Green)
                val origEndX = cx + vectorX * scale
                val origEndY = cy - vectorY * scale
                drawLine(color = MatrixGreen, start = Offset(cx, cy), end = Offset(origEndX, origEndY), strokeWidth = 3.dp.toPx())
                drawCircle(color = MatrixGreen, radius = 5.dp.toPx(), center = Offset(origEndX, origEndY))

                // Transformed Vector (Cyan)
                val transEndX = cx + transformedX * scale
                val transEndY = cy - transformedY * scale
                drawLine(color = NeonCyan, start = Offset(cx, cy), end = Offset(transEndX, transEndY), strokeWidth = 3.dp.toPx())
                drawCircle(color = NeonCyan, radius = 5.dp.toPx(), center = Offset(transEndX, transEndY))
            }
        }

        // Stats Readout
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidCard),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Original p:", color = MatrixGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "[ ${vectorX.toInt()}, ${vectorY.toInt()} ]ᵀ", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Matrix A:", color = SingularityGold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "[ ${activeMatrix.a.toInt()}  ${activeMatrix.b.toInt()} ; ${activeMatrix.c.toInt()}  ${activeMatrix.d.toInt()} ]", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Transformed p':", color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "[ ${transformedX.toInt()}, ${transformedY.toInt()} ]ᵀ", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Transformation Buttons
        Text(text = "Select Mathematical Operation:", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = {
                    activeMatrix = Matrix2x2.ROTATION_90
                    matrixName = "Rotation 90° Clockwise"
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (activeMatrix == Matrix2x2.ROTATION_90) NeonCyan else VoidElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("R(90°)", fontSize = 10.sp, color = if (activeMatrix == Matrix2x2.ROTATION_90) VoidBlack else TextPrimary)
            }
            Button(
                onClick = {
                    activeMatrix = Matrix2x2.ROTATION_180
                    matrixName = "Rotation 180°"
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (activeMatrix == Matrix2x2.ROTATION_180) NeonCyan else VoidElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("R(180°)", fontSize = 10.sp, color = if (activeMatrix == Matrix2x2.ROTATION_180) VoidBlack else TextPrimary)
            }
            Button(
                onClick = {
                    activeMatrix = Matrix2x2.REFLECTION_HORIZONTAL
                    matrixName = "Mirror X"
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (activeMatrix == Matrix2x2.REFLECTION_HORIZONTAL) NeonCyan else VoidElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Flip Y", fontSize = 10.sp, color = if (activeMatrix == Matrix2x2.REFLECTION_HORIZONTAL) VoidBlack else TextPrimary)
            }
            Button(
                onClick = {
                    activeMatrix = Matrix2x2.REFLECTION_VERTICAL
                    matrixName = "Mirror Y"
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (activeMatrix == Matrix2x2.REFLECTION_VERTICAL) NeonCyan else VoidElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Flip X", fontSize = 10.sp, color = if (activeMatrix == Matrix2x2.REFLECTION_VERTICAL) VoidBlack else TextPrimary)
            }
        }
    }
}

@Composable
private fun DeterminantsLab(onReward: (Int) -> Unit) {
    var a by remember { mutableFloatStateOf(1f) }
    var b by remember { mutableFloatStateOf(2f) }
    var c by remember { mutableFloatStateOf(2f) }
    var d by remember { mutableFloatStateOf(3f) }
    var rewardClaimed by remember { mutableStateOf(false) }

    val matrix = Matrix2x2(a, b, c, d)
    val det = matrix.det
    val isSingular = matrix.isSingular

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Determinants & Singularity Collapse",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SingularityGold
                )
                Text(
                    text = "The determinant det(A) = ad - bc measures signed area scaling. When det(A) = 0, the matrix collapses 2D space into a line or point—this is the mathematics behind Singularity Strike!",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Parallelogram Area Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VoidSurface)
                .border(1.dp, if (isSingular) SingularityGold else GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val scale = 25f

                // Origin
                val o = Offset(cx, cy)
                // v1 = [a, c]
                val p1 = Offset(cx + a * scale, cy - c * scale)
                // v2 = [b, d]
                val p2 = Offset(cx + b * scale, cy - d * scale)
                // v1 + v2
                val p3 = Offset(cx + (a + b) * scale, cy - (c + d) * scale)

                val path = Path().apply {
                    moveTo(o.x, o.y)
                    lineTo(p1.x, p1.y)
                    lineTo(p3.x, p3.y)
                    lineTo(p2.x, p2.y)
                    close()
                }

                drawPath(path, (if (isSingular) AlertRed else SingularityGold).copy(alpha = 0.35f))
                drawPath(path, if (isSingular) AlertRed else SingularityGold, style = Stroke(2.dp.toPx()))

                // Basis vectors
                drawLine(color = NeonCyan, start = o, end = p1, strokeWidth = 2.dp.toPx())
                drawLine(color = MatrixGreen, start = o, end = p2, strokeWidth = 2.dp.toPx())
            }
        }

        // Determinant value badge
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isSingular) AlertRed.copy(alpha = 0.2f) else VoidCard),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, if (isSingular) AlertRed else GridLineColor, RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "det(A) = ($a)($d) - ($b)($c)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "det(A) = ${String.format("%.2f", det)}",
                        color = if (isSingular) SingularityGold else TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (isSingular) {
                    Text(
                        text = "SINGULAR TENSOR!",
                        color = AlertRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Steppers for a, b, c, d
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MatrixEntryAdjuster("a", a, { a += 1f }, { a -= 1f }, Modifier.weight(1f))
            MatrixEntryAdjuster("b", b, { b += 1f }, { b -= 1f }, Modifier.weight(1f))
            MatrixEntryAdjuster("c", c, { c += 1f }, { c -= 1f }, Modifier.weight(1f))
            MatrixEntryAdjuster("d", d, { d += 1f }, { d -= 1f }, Modifier.weight(1f))
        }

        // Quick Preset to trigger Singularity det=0
        Button(
            onClick = {
                a = 1f; b = 2f; c = 2f; d = 4f
                if (!rewardClaimed) {
                    rewardClaimed = true
                    onReward(100)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SingularityGold),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SET SINGULAR MATRIX [1, 2; 2, 4] (det=0)", color = VoidBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MatrixEntryAdjuster(
    label: String,
    value: Float,
    onInc: () -> Unit,
    onDec: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VoidSurface),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier.border(1.dp, GridLineColor, RoundedCornerShape(6.dp))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text(text = "${value.toInt()}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "-",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDec() }.padding(4.dp)
                )
                Text(
                    text = "+",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onInc() }.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun EigenvaluesLab() {
    val matrix = Matrix2x2(3f, 1f, 1f, 3f)
    val eigenvalues = matrix.calculateEigenvalues()
    val dominantVec = matrix.calculateDominantEigenvector()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Eigenvalues & Resonant Eigenvectors",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlasmaMagenta
                )
                Text(
                    text = "Av = λv: An eigenvector v does not change direction under transformation A, only its scale λ. Aligning attacks with an enemy barrier's dominant eigenvector causes constructive resonance for 300% critical damage!",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VoidSurface)
                .border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val scale = 65f

                // Axes
                drawLine(color = GridLineColor, start = Offset(0f, cy), end = Offset(size.width, cy), strokeWidth = 1.dp.toPx())
                drawLine(color = GridLineColor, start = Offset(cx, 0f), end = Offset(cx, size.height), strokeWidth = 1.dp.toPx())

                // Dominant Eigenvector Ray (Magenta)
                val ex = dominantVec.first * scale
                val ey = -dominantVec.second * scale
                drawLine(
                    color = PlasmaMagenta,
                    start = Offset(cx - ex * 1.5f, cy - ey * 1.5f),
                    end = Offset(cx + ex * 1.5f, cy + ey * 1.5f),
                    strokeWidth = 3.dp.toPx()
                )
                drawCircle(color = PlasmaMagenta, radius = 6.dp.toPx(), center = Offset(cx + ex, cy + ey))
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidCard),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Matrix A = [ 3  1 ; 1  3 ]", color = NeonCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Characteristic Equation: λ² - 6λ + 8 = 0", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "Eigenvalues: λ₁ = ${eigenvalues?.first?.toInt()}, λ₂ = ${eigenvalues?.second?.toInt()}", color = SingularityGold, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text(text = "Dominant Resonant Vector v = [ ${String.format("%.2f", dominantVec.first)}, ${String.format("%.2f", dominantVec.second)} ]ᵀ", color = PlasmaMagenta, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun PdeDiffusionLab() {
    var stepCount by remember { mutableIntStateOf(0) }
    var diffusionRate by remember { mutableFloatStateOf(0.25f) }
    var centerHeat by remember { mutableFloatStateOf(100f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VoidElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Continuous PDE Diffusion Dynamics",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MatrixGreen
                )
                Text(
                    text = "∂E/∂t = D ∇²E - γE + S: Continuous energy heat spreads dynamically from flux reactors. Numerical discretization models continuous physical laws on the discrete grid.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Mini Grid heat display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(10.dp))
                .background(VoidSurface)
                .border(1.dp, GridLineColor, RoundedCornerShape(10.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridDim = 5
                val cellW = size.width / gridDim
                val cellH = size.height / gridDim

                for (y in 0 until gridDim) {
                    for (x in 0 until gridDim) {
                        val distFromCenter = Math.abs(x - 2) + Math.abs(y - 2)
                        val heat = (centerHeat / (1f + distFromCenter * (1f - diffusionRate * stepCount.coerceAtMost(3)))).coerceIn(0f, 100f)

                        drawRect(
                            color = SingularityGold.copy(alpha = (heat / 100f) * 0.7f),
                            topLeft = Offset(x * cellW, y * cellH),
                            size = Size(cellW, cellH)
                        )
                        drawRect(
                            color = GridLineColor,
                            topLeft = Offset(x * cellW, y * cellH),
                            size = Size(cellW, cellH),
                            style = Stroke(1.dp.toPx())
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { stepCount++ },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("STEP PDE TIME Δt (Steps: $stepCount)", color = VoidBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { stepCount = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = VoidElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.width(80.dp)
            ) {
                Text("RESET", color = TextPrimary, fontSize = 10.sp)
            }
        }
    }
}
