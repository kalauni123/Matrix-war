package com.example.engine

import com.example.model.CellType
import com.example.model.EigenBarrier
import com.example.model.FieldCell
import com.example.model.GridPos
import com.example.model.Matrix2x2
import com.example.model.TacticalUnit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MatrixMathEngine {

    /**
     * Applies a 2x2 matrix transformation around a pivot point:
     * p' = M * (p - pivot) + pivot
     */
    fun transformAroundPivot(
        source: GridPos,
        pivot: GridPos,
        matrix: Matrix2x2,
        gridWidth: Int = 10,
        gridHeight: Int = 10,
        occupiedPositions: Set<GridPos> = emptySet()
    ): GridPos {
        val relX = (source.x - pivot.x).toFloat()
        val relY = (source.y - pivot.y).toFloat()

        val transformedRelX = matrix.a * relX + matrix.b * relY
        val transformedRelY = matrix.c * relX + matrix.d * relY

        var targetX = Math.round(transformedRelX + pivot.x)
        var targetY = Math.round(transformedRelY + pivot.y)

        // Clamp to grid bounds
        targetX = targetX.coerceIn(0, gridWidth - 1)
        targetY = targetY.coerceIn(0, gridHeight - 1)

        val candidate = GridPos(targetX, targetY)
        // If candidate is already occupied by another unit, find closest adjacent free cell
        return if (occupiedPositions.contains(candidate)) {
            findClosestFreeCell(candidate, occupiedPositions, gridWidth, gridHeight) ?: source
        } else {
            candidate
        }
    }

    private fun findClosestFreeCell(
        origin: GridPos,
        occupied: Set<GridPos>,
        width: Int,
        height: Int
    ): GridPos? {
        val deltas = listOf(
            GridPos(1, 0), GridPos(-1, 0), GridPos(0, 1), GridPos(0, -1),
            GridPos(1, 1), GridPos(-1, 1), GridPos(1, -1), GridPos(-1, -1)
        )
        return deltas.map { origin + it }
            .filter { it.isWithinBounds(width, height) && !occupied.contains(it) }
            .minByOrNull { it.manhattanDistance(origin) }
    }

    /**
     * Discrete Diffusion PDE simulation step:
     * dE/dt = D * Laplacian(E) - gamma * E + Source
     */
    fun stepDiffusionPde(
        currentCells: Map<GridPos, FieldCell>,
        width: Int = 10,
        height: Int = 10,
        diffusionCoeff: Float = 0.22f,
        dissipationRate: Float = 0.04f
    ): Map<GridPos, FieldCell> {
        val updated = mutableMapOf<GridPos, FieldCell>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pos = GridPos(x, y)
                val current = currentCells[pos] ?: FieldCell(pos)
                val eCurrent = current.diffusionEnergy

                // Calculate discrete 5-point stencil Laplacian
                val eLeft = currentCells[GridPos(x - 1, y)]?.diffusionEnergy ?: eCurrent
                val eRight = currentCells[GridPos(x + 1, y)]?.diffusionEnergy ?: eCurrent
                val eTop = currentCells[GridPos(x, y - 1)]?.diffusionEnergy ?: eCurrent
                val eBottom = currentCells[GridPos(x, y + 1)]?.diffusionEnergy ?: eCurrent

                val laplacian = (eLeft + eRight + eTop + eBottom) - 4f * eCurrent

                // Source term from Reactor cells
                val source = when (current.type) {
                    CellType.FLUX_REACTOR -> 35f
                    CellType.ENERGY_NODE -> 12f
                    CellType.HAZARD_ZONE -> -5f
                    else -> 0f
                }

                val deltaE = diffusionCoeff * laplacian - dissipationRate * eCurrent + source
                val nextE = (eCurrent + deltaE).coerceIn(0f, 100f)

                updated[pos] = current.copy(diffusionEnergy = nextE)
            }
        }
        return updated
    }

    /**
     * Traces vector projectile with mirror ricochet reflections:
     * Reflective surface normal vector n; reflected ray r = d - 2(d dot n)n
     */
    fun traceRicochetPath(
        start: GridPos,
        direction: GridPos,
        cells: Map<GridPos, FieldCell>,
        width: Int = 10,
        height: Int = 10,
        maxSteps: Int = 14
    ): List<GridPos> {
        val path = mutableListOf<GridPos>()
        var curr = start
        var dirX = direction.x.coerceIn(-1, 1)
        var dirY = direction.y.coerceIn(-1, 1)
        if (dirX == 0 && dirY == 0) dirX = 1

        path.add(curr)
        var step = 0

        while (step < maxSteps) {
            val nextX = curr.x + dirX
            val nextY = curr.y + dirY
            val nextPos = GridPos(nextX, nextY)

            // Grid boundary bounce
            if (!nextPos.isWithinBounds(width, height)) {
                if (nextX < 0 || nextX >= width) dirX = -dirX
                if (nextY < 0 || nextY >= height) dirY = -dirY
                step++
                continue
            }

            path.add(nextPos)
            val cell = cells[nextPos]

            // Mirror Wall reflection
            if (cell?.type == CellType.MIRROR_WALL) {
                // Specular reflection (swap components or invert)
                val temp = dirX
                dirX = -dirY
                dirY = temp
            }

            curr = nextPos
            step++
        }
        return path
    }

    /**
     * Computes directional shield damage reduction using dot product.
     * Normal vector n = [cos(shieldAngle), sin(shieldAngle)]
     * Attack vector v = [attacker.x - target.x, attacker.y - target.y] normalized
     * If dot(v, n) > 0.4 (attack hitting directly against the shield front), damage is mitigated by up to 70%!
     */
    fun calculateShieldMitigation(
        target: TacticalUnit,
        attackerPos: GridPos,
        incomingDamage: Int
    ): Pair<Int, Boolean> {
        if (!target.isShieldActive) return Pair(incomingDamage, false)

        val dx = (attackerPos.x - target.pos.x).toFloat()
        val dy = (attackerPos.y - target.pos.y).toFloat()
        val len = sqrt(dx * dx + dy * dy)
        if (len < 0.001f) return Pair(incomingDamage, false)

        val attackNormX = dx / len
        val attackNormY = dy / len

        val rad = target.shieldAngleDeg * (PI.toFloat() / 180f)
        val shieldNormX = cos(rad)
        val shieldNormY = sin(rad)

        val dotProduct = attackNormX * shieldNormX + attackNormY * shieldNormY

        return if (dotProduct > 0.35f) {
            // Frontal defense! 70% damage reduction
            val reduced = Math.round(incomingDamage * 0.30f).coerceAtLeast(4)
            Pair(reduced, true)
        } else {
            // Flanked! Full damage
            Pair(incomingDamage, false)
        }
    }

    /**
     * Evaluates Eigen Resonance for an attack against an active EigenBarrier.
     * If the attack angle matches the barrier's dominant eigenvector direction,
     * it causes a constructive harmonic resonance dealing 300% critical damage!
     */
    fun checkEigenResonance(
        attackerPos: GridPos,
        targetPos: GridPos,
        barrier: EigenBarrier
    ): Pair<Float, Boolean> {
        val dx = (targetPos.x - attackerPos.x).toFloat()
        val dy = (targetPos.y - attackerPos.y).toFloat()
        val attackAngleRad = atan2(dy, dx)
        var attackAngleDeg = attackAngleRad * (180f / PI.toFloat())
        if (attackAngleDeg < 0) attackAngleDeg += 360f

        val barrierAngle = barrier.resonantAngleDeg
        val angleDiff = abs(attackAngleDeg - barrierAngle)
        val normalizedDiff = if (angleDiff > 180f) 360f - angleDiff else angleDiff

        return if (normalizedDiff <= 32f) {
            // Harmonic Resonance achieved!
            Pair(3.0f, true)
        } else {
            Pair(1.0f, false)
        }
    }
}
