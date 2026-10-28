package com.example.engine

import com.example.model.BattleState
import com.example.model.CellType
import com.example.model.GridPos
import com.example.model.TacticalUnit
import com.example.model.UnitClass
import com.example.model.UnitFaction

sealed class AiAction {
    data class Move(val unitId: String, val targetPos: GridPos) : AiAction()
    data class Attack(val attackerId: String, val targetId: String) : AiAction()
    data class SpatialRotate(val unitId: String, val pivot: GridPos) : AiAction()
    data class ActivateShield(val unitId: String, val angleDeg: Float) : AiAction()
    object PassTurn : AiAction()
}

object TacticalAiEngine {

    /**
     * Determines optimal tactical actions for enemy AI units based on Game Theory utility evaluation.
     */
    fun computeNextAction(state: BattleState): AiAction? {
        val livingEnemies = state.enemyUnits.filter { !it.hasActedThisTurn }
        if (livingEnemies.isEmpty()) return null

        val livingPlayers = state.playerUnits
        if (livingPlayers.isEmpty()) return null

        // Pick highest priority enemy unit to act
        val actingUnit = livingEnemies.maxByOrNull { unit ->
            when (unit.unitClass) {
                UnitClass.COMMANDER -> 100
                UnitClass.SINGULARITY_CORE -> 85
                UnitClass.SNIPER -> 75
                UnitClass.STRIKER -> 65
                UnitClass.DEFENDER -> 50
            }
        } ?: return null

        // 1. Can we attack a player in range?
        val targetInRange = livingPlayers
            .filter { player -> actingUnit.pos.manhattanDistance(player.pos) <= actingUnit.unitClass.attackRange }
            .minByOrNull { it.hp }

        if (targetInRange != null) {
            return AiAction.Attack(actingUnit.id, targetInRange.id)
        }

        // 2. Can we use directional shield if defender or low HP?
        if (actingUnit.unitClass == UnitClass.DEFENDER && !actingUnit.isShieldActive) {
            val closestPlayer = livingPlayers.minByOrNull { it.pos.manhattanDistance(actingUnit.pos) }
            if (closestPlayer != null) {
                val dx = (closestPlayer.pos.x - actingUnit.pos.x).toFloat()
                val dy = (closestPlayer.pos.y - actingUnit.pos.y).toFloat()
                val angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
                return AiAction.ActivateShield(actingUnit.id, (angle + 360f) % 360f)
            }
        }

        // 3. Move toward closest strategic objective or player
        val occupied = state.units.filter { it.isAlive }.map { it.pos }.toSet()
        val closestPlayer = livingPlayers.minByOrNull { it.pos.manhattanDistance(actingUnit.pos) }

        if (closestPlayer != null) {
            val bestMove = findBestStep(
                current = actingUnit.pos,
                target = closestPlayer.pos,
                range = actingUnit.unitClass.moveRange,
                occupied = occupied,
                cells = state.cells,
                width = state.gridWidth,
                height = state.gridHeight
            )
            if (bestMove != null && bestMove != actingUnit.pos) {
                return AiAction.Move(actingUnit.id, bestMove)
            }
        }

        return AiAction.PassTurn
    }

    private fun findBestStep(
        current: GridPos,
        target: GridPos,
        range: Int,
        occupied: Set<GridPos>,
        cells: Map<GridPos, com.example.model.FieldCell>,
        width: Int,
        height: Int
    ): GridPos? {
        val candidates = mutableListOf<GridPos>()
        for (dx in -range..range) {
            for (dy in -range..range) {
                if (Math.abs(dx) + Math.abs(dy) in 1..range) {
                    val candidate = GridPos(current.x + dx, current.y + dy)
                    if (candidate.isWithinBounds(width, height) && !occupied.contains(candidate)) {
                        candidates.add(candidate)
                    }
                }
            }
        }

        // Score candidates based on distance to target and tactical cell bonuses
        return candidates.minByOrNull { pos ->
            val dist = pos.manhattanDistance(target) * 10
            val cellBonus = when (cells[pos]?.type) {
                CellType.ENERGY_NODE -> -15
                CellType.DEFENSE_NODE -> -10
                CellType.HAZARD_ZONE -> 50 // Avoid hazard
                else -> 0
            }
            dist + cellBonus
        }
    }
}
