package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BattleState
import com.example.model.CellType
import com.example.model.GridPos
import com.example.model.TacticalUnit
import com.example.model.UnitClass
import com.example.model.UnitFaction
import com.example.ui.theme.AlertRed
import com.example.ui.theme.GridLineColor
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlasmaMagenta
import com.example.ui.theme.QuantumBlue
import com.example.ui.theme.SingularityGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidElevated
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TacticalGridView(
    state: BattleState,
    onCellClick: (GridPos) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(VoidBlack)
            .border(1.dp, GridLineColor, RoundedCornerShape(12.dp))
            .testTag("tactical_grid_view")
    ) {
        val totalWidth = constraints.maxWidth.toFloat()
        val totalHeight = constraints.maxHeight.toFloat()
        val cellSize = totalWidth / state.gridWidth

        val selectedUnit = state.selectedUnit
        val validMovePositions = if (selectedUnit != null && selectedUnit.faction == UnitFaction.PLAYER && selectedUnit.movesRemaining > 0) {
            getValidMoves(selectedUnit, state)
        } else emptySet()

        val validAttackPositions = if (selectedUnit != null && selectedUnit.faction == UnitFaction.PLAYER && !selectedUnit.hasActedThisTurn) {
            getValidAttacks(selectedUnit, state)
        } else emptySet()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val cellX = (offset.x / cellSize).toInt().coerceIn(0, state.gridWidth - 1)
                        val cellY = (offset.y / cellSize).toInt().coerceIn(0, state.gridHeight - 1)
                        onCellClick(GridPos(cellX, cellY))
                    }
                }
        ) {
            // 1. Draw Grid lines and Background Cells
            drawGridCells(state, cellSize, pulseAlpha)

            // 2. Draw Valid Move Highlights
            for (pos in validMovePositions) {
                val cx = pos.x * cellSize + cellSize / 2f
                val cy = pos.y * cellSize + cellSize / 2f
                drawCircle(
                    color = MatrixGreen.copy(alpha = 0.35f),
                    radius = cellSize * 0.35f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = MatrixGreen,
                    radius = cellSize * 0.12f,
                    center = Offset(cx, cy)
                )
            }

            // 3. Draw Valid Attack Highlights
            for (pos in validAttackPositions) {
                val cx = pos.x * cellSize + cellSize / 2f
                val cy = pos.y * cellSize + cellSize / 2f
                drawCircle(
                    color = AlertRed.copy(alpha = 0.3f),
                    radius = cellSize * 0.4f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = AlertRed,
                    radius = cellSize * 0.14f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 4. Draw Ricochet Trajectory Beam
            if (state.ricochetPath.size >= 2) {
                drawRicochetBeam(state.ricochetPath, cellSize, pulseAlpha)
            }

            // 5. Draw Transformation Preview (ghost units)
            for ((unitId, targetPos) in state.transformationPreview) {
                val unit = state.units.find { it.id == unitId }
                if (unit != null) {
                    val px = targetPos.x * cellSize
                    val py = targetPos.y * cellSize
                    drawRoundRect(
                        color = NeonCyan.copy(alpha = 0.3f),
                        topLeft = Offset(px + 4f, py + 4f),
                        size = Size(cellSize - 8f, cellSize - 8f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )
                }
            }

            // 6. Draw Transformation Pivot Crosshair
            state.selectedPivot?.let { pivot ->
                val px = pivot.x * cellSize + cellSize / 2f
                val py = pivot.y * cellSize + cellSize / 2f
                drawCircle(
                    color = SingularityGold,
                    radius = cellSize * 0.45f,
                    center = Offset(px, py),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawLine(
                    color = SingularityGold,
                    start = Offset(px - cellSize * 0.4f, py),
                    end = Offset(px + cellSize * 0.4f, py),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = SingularityGold,
                    start = Offset(px, py - cellSize * 0.4f),
                    end = Offset(px, py + cellSize * 0.4f),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 7. Draw Units
            for (unit in state.units.filter { it.isAlive }) {
                drawUnit(unit, cellSize, unit.id == state.selectedUnitId, pulseAlpha)
            }
        }
    }
}

private fun DrawScope.drawGridCells(
    state: BattleState,
    cellSize: Float,
    pulseAlpha: Float
) {
    for (y in 0 until state.gridHeight) {
        for (x in 0 until state.gridWidth) {
            val pos = GridPos(x, y)
            val cell = state.cells[pos]
            val left = x * cellSize
            val top = y * cellSize

            // PDE continuous heat diffusion background tint
            val heat = cell?.heatIntensity ?: 0f
            if (heat > 0.05f) {
                drawRect(
                    color = SingularityGold.copy(alpha = heat * 0.35f),
                    topLeft = Offset(left, top),
                    size = Size(cellSize, cellSize)
                )
            }

            // Grid cell borders
            drawRect(
                color = GridLineColor.copy(alpha = 0.5f),
                topLeft = Offset(left, top),
                size = Size(cellSize, cellSize),
                style = Stroke(width = 0.8.dp.toPx())
            )

            // Draw special tactical cell markers
            when (cell?.type) {
                CellType.ENERGY_NODE -> {
                    // Cyan Diamond
                    val path = Path().apply {
                        moveTo(left + cellSize / 2f, top + 4f)
                        lineTo(left + cellSize - 4f, top + cellSize / 2f)
                        lineTo(left + cellSize / 2f, top + cellSize - 4f)
                        lineTo(left + 4f, top + cellSize / 2f)
                        close()
                    }
                    drawPath(path, NeonCyan.copy(alpha = 0.25f))
                    drawPath(path, NeonCyan.copy(alpha = 0.7f), style = Stroke(1.2.dp.toPx()))
                }
                CellType.DEFENSE_NODE -> {
                    // Blue Shield Icon outline
                    drawRoundRect(
                        color = QuantumBlue.copy(alpha = 0.22f),
                        topLeft = Offset(left + 5f, top + 5f),
                        size = Size(cellSize - 10f, cellSize - 10f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        color = QuantumBlue.copy(alpha = 0.7f),
                        topLeft = Offset(left + 5f, top + 5f),
                        size = Size(cellSize - 10f, cellSize - 10f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                        style = Stroke(1.2.dp.toPx())
                    )
                }
                CellType.TRANSFORMATION_ZONE -> {
                    // Magenta Warp Circle
                    drawCircle(
                        color = PlasmaMagenta.copy(alpha = 0.25f),
                        radius = cellSize * 0.36f,
                        center = Offset(left + cellSize / 2f, top + cellSize / 2f)
                    )
                    drawCircle(
                        color = PlasmaMagenta.copy(alpha = 0.8f),
                        radius = cellSize * 0.36f,
                        center = Offset(left + cellSize / 2f, top + cellSize / 2f),
                        style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f))
                    )
                }
                CellType.HAZARD_ZONE -> {
                    // Red Hazard Striping
                    drawRect(
                        color = AlertRed.copy(alpha = 0.2f),
                        topLeft = Offset(left, top),
                        size = Size(cellSize, cellSize)
                    )
                    drawLine(
                        color = AlertRed.copy(alpha = 0.6f),
                        start = Offset(left + 3f, top + cellSize - 3f),
                        end = Offset(left + cellSize - 3f, top + 3f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
                CellType.FLUX_REACTOR -> {
                    // High-energy PDE reactor core
                    drawCircle(
                        color = SingularityGold.copy(alpha = 0.35f * pulseAlpha),
                        radius = cellSize * 0.42f,
                        center = Offset(left + cellSize / 2f, top + cellSize / 2f)
                    )
                    drawCircle(
                        color = SingularityGold,
                        radius = cellSize * 0.2f,
                        center = Offset(left + cellSize / 2f, top + cellSize / 2f)
                    )
                }
                CellType.MIRROR_WALL -> {
                    // Bright reflective silver prism wall
                    drawRect(
                        color = Color(0xFFC0D8F0).copy(alpha = 0.5f),
                        topLeft = Offset(left + 4f, top + 4f),
                        size = Size(cellSize - 8f, cellSize - 8f)
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(left + 4f, top + 4f),
                        end = Offset(left + cellSize - 4f, top + cellSize - 4f),
                        strokeWidth = 2.5.dp.toPx()
                    )
                }
                else -> {}
            }
        }
    }
}

private fun DrawScope.drawUnit(
    unit: TacticalUnit,
    cellSize: Float,
    isSelected: Boolean,
    pulseAlpha: Float
) {
    val px = unit.pos.x * cellSize
    val py = unit.pos.y * cellSize
    val cx = px + cellSize / 2f
    val cy = py + cellSize / 2f
    val radius = cellSize * 0.38f

    val isPlayer = unit.faction == UnitFaction.PLAYER
    val baseColor = if (isPlayer) NeonCyan else AlertRed
    val secondaryColor = if (isPlayer) MatrixGreen else PlasmaMagenta

    // Selection Halo
    if (isSelected) {
        drawCircle(
            color = if (isPlayer) MatrixGreen.copy(alpha = 0.4f * pulseAlpha) else AlertRed.copy(alpha = 0.4f),
            radius = radius * 1.35f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = if (isPlayer) MatrixGreen else AlertRed,
            radius = radius * 1.35f,
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )
    }

    // Unit Body
    when (unit.unitClass) {
        UnitClass.COMMANDER -> {
            // Hexagon / Crown shape
            val path = Path().apply {
                for (i in 0 until 6) {
                    val angle = (i * 60f + 30f) * (PI.toFloat() / 180f)
                    val x = cx + radius * cos(angle)
                    val y = cy + radius * sin(angle)
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(path, baseColor.copy(alpha = 0.25f))
            drawPath(path, baseColor, style = Stroke(width = 2.5.dp.toPx()))

            // Core inner jewel
            drawCircle(color = SingularityGold, radius = radius * 0.35f, center = Offset(cx, cy))
        }
        UnitClass.STRIKER -> {
            // Vector Arrow / Chevron shape pointing up
            val path = Path().apply {
                moveTo(cx, cy - radius)
                lineTo(cx + radius, cy + radius * 0.7f)
                lineTo(cx, cy + radius * 0.2f)
                lineTo(cx - radius, cy + radius * 0.7f)
                close()
            }
            drawPath(path, baseColor.copy(alpha = 0.3f))
            drawPath(path, baseColor, style = Stroke(width = 2.dp.toPx()))
        }
        UnitClass.DEFENDER -> {
            // Fortress Square
            drawRoundRect(
                color = baseColor.copy(alpha = 0.3f),
                topLeft = Offset(cx - radius * 0.85f, cy - radius * 0.85f),
                size = Size(radius * 1.7f, radius * 1.7f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = baseColor,
                topLeft = Offset(cx - radius * 0.85f, cy - radius * 0.85f),
                size = Size(radius * 1.7f, radius * 1.7f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        UnitClass.SNIPER -> {
            // Diamond with crosshair
            val path = Path().apply {
                moveTo(cx, cy - radius)
                lineTo(cx + radius, cy)
                lineTo(cx, cy + radius)
                lineTo(cx - radius, cy)
                close()
            }
            drawPath(path, baseColor.copy(alpha = 0.3f))
            drawPath(path, baseColor, style = Stroke(width = 2.dp.toPx()))
            drawCircle(color = secondaryColor, radius = radius * 0.25f, center = Offset(cx, cy))
        }
        UnitClass.SINGULARITY_CORE -> {
            // Concentric Singularity Rings
            drawCircle(color = baseColor.copy(alpha = 0.2f), radius = radius, center = Offset(cx, cy))
            drawCircle(
                color = SingularityGold,
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 1.8.dp.toPx())
            )
            drawCircle(
                color = PlasmaMagenta,
                radius = radius * 0.5f,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }

    // Directional Shield Arc (if shield active)
    if (unit.isShieldActive) {
        val shieldRad = (unit.shieldAngleDeg) * (PI.toFloat() / 180f)
        val arcRadius = radius * 1.35f
        drawArc(
            color = QuantumBlue,
            startAngle = unit.shieldAngleDeg - 50f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(cx - arcRadius, cy - arcRadius),
            size = Size(arcRadius * 2f, arcRadius * 2f),
            style = Stroke(width = 3.5.dp.toPx())
        )
    }

    // Health Bar mini-gauge at bottom of cell
    val barWidth = cellSize * 0.8f
    val barHeight = 3.5.dp.toPx()
    val barLeft = cx - barWidth / 2f
    val barTop = py + cellSize - 5.5.dp.toPx()

    // Background
    drawRect(
        color = Color(0xFF1E2638),
        topLeft = Offset(barLeft, barTop),
        size = Size(barWidth, barHeight)
    )
    // Health Fill
    drawRect(
        color = if (unit.hpPercent > 0.45f) MatrixGreen else if (unit.hpPercent > 0.2f) SingularityGold else AlertRed,
        topLeft = Offset(barLeft, barTop),
        size = Size(barWidth * unit.hpPercent, barHeight)
    )
}

private fun DrawScope.drawRicochetBeam(
    path: List<GridPos>,
    cellSize: Float,
    pulseAlpha: Float
) {
    if (path.size < 2) return
    val beamPath = Path().apply {
        val first = path.first()
        moveTo(first.x * cellSize + cellSize / 2f, first.y * cellSize + cellSize / 2f)
        for (i in 1 until path.size) {
            val p = path[i]
            lineTo(p.x * cellSize + cellSize / 2f, p.y * cellSize + cellSize / 2f)
        }
    }
    // Outer laser glow
    drawPath(
        path = beamPath,
        color = NeonCyan.copy(alpha = 0.45f * pulseAlpha),
        style = Stroke(width = 6.dp.toPx())
    )
    // Core laser beam
    drawPath(
        path = beamPath,
        color = Color.White,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun getValidMoves(unit: TacticalUnit, state: BattleState): Set<GridPos> {
    val moves = mutableSetOf<GridPos>()
    val range = unit.unitClass.moveRange
    val livingPos = state.units.filter { it.isAlive }.map { it.pos }.toSet()

    for (dx in -range..range) {
        for (dy in -range..range) {
            if (kotlin.math.abs(dx) + kotlin.math.abs(dy) in 1..range) {
                val candidate = GridPos(unit.pos.x + dx, unit.pos.y + dy)
                if (candidate.isWithinBounds(state.gridWidth, state.gridHeight) && !livingPos.contains(candidate)) {
                    moves.add(candidate)
                }
            }
        }
    }
    return moves
}

private fun getValidAttacks(unit: TacticalUnit, state: BattleState): Set<GridPos> {
    val range = unit.unitClass.attackRange
    return state.enemyUnits.filter { enemy ->
        unit.pos.manhattanDistance(enemy.pos) <= range
    }.map { it.pos }.toSet()
}
