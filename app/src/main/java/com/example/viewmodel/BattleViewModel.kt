package com.example.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.engine.AiAction
import com.example.engine.MatrixMathEngine
import com.example.engine.TacticalAiEngine
import com.example.model.AbilityType
import com.example.model.BattlePhase
import com.example.model.BattleState
import com.example.model.CellType
import com.example.model.CombatLogEntry
import com.example.model.EigenBarrier
import com.example.model.FieldCell
import com.example.model.GridPos
import com.example.model.Matrix2x2
import com.example.model.TacticalAbility
import com.example.model.TacticalUnit
import com.example.model.UnitClass
import com.example.model.UnitFaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BattleViewModel(
    private val repository: GameRepository,
    private val context: Context? = null
) : ViewModel() {

    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    init {
        initializeSector(1)
    }

    fun initializeSector(sectorNumber: Int) {
        val width = 10
        val height = 10
        val cells = mutableMapOf<GridPos, FieldCell>()

        // Generate Grid Cells with tactical nodes
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pos = GridPos(x, y)
                val type = when {
                    // Reflective mirrors for geometry
                    (x == 4 && y == 3) || (x == 5 && y == 6) || (x == 3 && y == 7) -> CellType.MIRROR_WALL
                    // PDE Thermal Flux Reactors
                    (x == 5 && y == 4) || (x == 4 && y == 5) -> CellType.FLUX_REACTOR
                    // Strategic Energy Nodes
                    (x == 2 && y == 2) || (x == 7 && y == 7) || (x == 8 && y == 2) || (x == 1 && y == 7) -> CellType.ENERGY_NODE
                    // Transformation acceleration zones
                    (x == 2 && y == 5) || (x == 7 && y == 4) -> CellType.TRANSFORMATION_ZONE
                    // Defensive Shield Matrix
                    (x == 3 && y == 3) || (x == 6 && y == 6) -> CellType.DEFENSE_NODE
                    // Hazard zones
                    (x == 0 && y == 4) || (x == 9 && y == 5) -> CellType.HAZARD_ZONE
                    else -> CellType.EMPTY
                }
                val initialHeat = if (type == CellType.FLUX_REACTOR) 60f else 0f
                cells[pos] = FieldCell(pos = pos, type = type, diffusionEnergy = initialHeat)
            }
        }

        // Setup Player Formation
        val playerUnits = listOf(
            TacticalUnit(
                id = "p_commander",
                name = "Commander Orion",
                faction = UnitFaction.PLAYER,
                unitClass = UnitClass.COMMANDER,
                pos = GridPos(1, 2),
                hp = 120,
                maxHp = 120,
                energy = 100,
                maxEnergy = 100
            ),
            TacticalUnit(
                id = "p_striker",
                name = "Striker Vega",
                faction = UnitFaction.PLAYER,
                unitClass = UnitClass.STRIKER,
                pos = GridPos(2, 3),
                hp = 80,
                maxHp = 80,
                energy = 60,
                maxEnergy = 60
            ),
            TacticalUnit(
                id = "p_defender",
                name = "Defender Aegis",
                faction = UnitFaction.PLAYER,
                unitClass = UnitClass.DEFENDER,
                pos = GridPos(1, 4),
                hp = 140,
                maxHp = 140,
                energy = 50,
                maxEnergy = 50
            ),
            TacticalUnit(
                id = "p_sniper",
                name = "Vector Lyra",
                faction = UnitFaction.PLAYER,
                unitClass = UnitClass.SNIPER,
                pos = GridPos(0, 3),
                hp = 65,
                maxHp = 65,
                energy = 70,
                maxEnergy = 70
            )
        )

        // Setup Enemy AI Formation
        val enemyUnits = listOf(
            TacticalUnit(
                id = "e_commander",
                name = "Sector Overseer",
                faction = UnitFaction.ENEMY_AI,
                unitClass = UnitClass.COMMANDER,
                pos = GridPos(8, 7),
                hp = 130,
                maxHp = 130,
                energy = 80,
                maxEnergy = 80
            ),
            TacticalUnit(
                id = "e_striker",
                name = "Null Striker",
                faction = UnitFaction.ENEMY_AI,
                unitClass = UnitClass.STRIKER,
                pos = GridPos(7, 6),
                hp = 75,
                maxHp = 75,
                energy = 50,
                maxEnergy = 50
            ),
            TacticalUnit(
                id = "e_sniper",
                name = "Vector Null",
                faction = UnitFaction.ENEMY_AI,
                unitClass = UnitClass.SNIPER,
                pos = GridPos(8, 5),
                hp = 60,
                maxHp = 60,
                energy = 60,
                maxEnergy = 60
            ),
            TacticalUnit(
                id = "e_defender",
                name = "Bastion Matrix",
                faction = UnitFaction.ENEMY_AI,
                unitClass = UnitClass.DEFENDER,
                pos = GridPos(6, 7),
                hp = 110,
                maxHp = 110,
                energy = 40,
                maxEnergy = 40
            )
        )

        val barrier = if (sectorNumber >= 4) {
            EigenBarrier(
                matrix = Matrix2x2(2f, 1f, 1f, 2f),
                resonantAngleDeg = 45f,
                shieldIntegrity = 150,
                maxIntegrity = 150
            )
        } else null

        _battleState.value = BattleState(
            gridWidth = width,
            gridHeight = height,
            cells = cells,
            units = playerUnits + enemyUnits,
            turnNumber = 1,
            phase = BattlePhase.PLAYER_TURN,
            playerEnergy = 90,
            maxPlayerEnergy = 100,
            playerActionPoints = 3,
            maxActionPoints = 3,
            aiEnergy = 60,
            sectorId = sectorNumber,
            sectorTitle = "Sector $sectorNumber",
            eigenBarrier = barrier,
            combatLogs = listOf(
                CombatLogEntry(
                    turn = 1,
                    message = "Tactical Grid initialized. Mathematical energy flows active.",
                    formula = "R(90°), det(A), ∇²E"
                )
            )
        )
    }

    fun onCellClicked(pos: GridPos) {
        val state = _battleState.value
        if (state.phase != BattlePhase.PLAYER_TURN) return

        val ability = state.selectedAbility
        val selectedUnit = state.selectedUnit

        // 1. Ability Targeting Mode
        if (ability != null) {
            handleAbilityTargeting(ability, pos)
            return
        }

        // 2. Unit Selection or Move/Attack Mode
        val clickedUnit = state.units.find { it.isAlive && it.pos == pos }

        if (clickedUnit != null) {
            if (clickedUnit.faction == UnitFaction.PLAYER) {
                // Select friendly unit
                _battleState.update {
                    it.copy(
                        selectedUnitId = clickedUnit.id,
                        selectedPivot = clickedUnit.pos,
                        ricochetPath = emptyList(),
                        transformationPreview = emptyMap()
                    )
                }
                vibrate(15)
            } else if (selectedUnit != null && selectedUnit.faction == UnitFaction.PLAYER) {
                // Attack hostile unit if in range
                val distance = selectedUnit.pos.manhattanDistance(clickedUnit.pos)
                if (distance <= selectedUnit.unitClass.attackRange && !selectedUnit.hasActedThisTurn && state.playerActionPoints > 0) {
                    executeAttack(selectedUnit, clickedUnit)
                }
            }
        } else if (selectedUnit != null && selectedUnit.faction == UnitFaction.PLAYER && selectedUnit.movesRemaining > 0) {
            // Move unit to empty cell if in range
            val distance = selectedUnit.pos.manhattanDistance(pos)
            if (distance <= selectedUnit.unitClass.moveRange) {
                executeMove(selectedUnit, pos)
            }
        }
    }

    fun onSelectAbility(ability: TacticalAbility) {
        val state = _battleState.value
        if (state.phase != BattlePhase.PLAYER_TURN) return

        if (state.selectedAbility?.type == ability.type) {
            // Deselect
            _battleState.update {
                it.copy(
                    selectedAbility = null,
                    transformationPreview = emptyMap(),
                    ricochetPath = emptyList()
                )
            }
            return
        }

        val pivot = state.selectedPivot ?: state.selectedUnit?.pos ?: GridPos(4, 4)

        // Generate transformation preview for rotation/reflection
        val preview = mutableMapOf<String, GridPos>()
        if (ability.matrixRep != null) {
            val friendlyUnits = state.playerUnits
            for (unit in friendlyUnits) {
                val newPos = MatrixMathEngine.transformAroundPivot(
                    source = unit.pos,
                    pivot = pivot,
                    matrix = ability.matrixRep,
                    gridWidth = state.gridWidth,
                    gridHeight = state.gridHeight
                )
                preview[unit.id] = newPos
            }
        }

        // Preview Ricochet path if sniper
        val selectedUnit = state.selectedUnit
        val ricochetPath = if (ability.type == AbilityType.RICOCHET_BEAM && selectedUnit != null) {
            MatrixMathEngine.traceRicochetPath(
                start = selectedUnit.pos,
                direction = GridPos(1, 1),
                cells = state.cells,
                width = state.gridWidth,
                height = state.gridHeight
            )
        } else emptyList()

        _battleState.update {
            it.copy(
                selectedAbility = ability,
                selectedPivot = pivot,
                transformationPreview = preview,
                ricochetPath = ricochetPath
            )
        }
        vibrate(20)
    }

    private fun handleAbilityTargeting(ability: TacticalAbility, targetPos: GridPos) {
        val state = _battleState.value
        val pivot = state.selectedPivot ?: targetPos
        val unit = state.selectedUnit

        when (ability.type) {
            AbilityType.SPATIAL_SHIFT, AbilityType.MIRROR_STRIKE -> {
                // Execute matrix transformation around pivot
                val matrix = ability.matrixRep ?: Matrix2x2.IDENTITY
                executeMatrixTransformation(matrix, pivot, ability)
            }
            AbilityType.VECTOR_DASH -> {
                // Translate selected unit directly to targetPos
                if (unit != null && targetPos.manhattanDistance(unit.pos) <= 3) {
                    executeMove(unit, targetPos, isDash = true)
                    consumeResources(ability.energyCost, ability.actionPointCost)
                    clearAbilitySelection()
                }
            }
            AbilityType.SINGULARITY_STRIKE -> {
                // Execute Singularity det=0 collapse on target unit
                val targetUnit = state.enemyUnits.find { it.pos == targetPos }
                if (targetUnit != null) {
                    executeSingularityStrike(targetUnit, ability)
                }
            }
            AbilityType.EIGEN_WEAPON -> {
                // Resonant eigen strike
                val targetUnit = state.enemyUnits.find { it.pos == targetPos }
                if (targetUnit != null && unit != null) {
                    executeEigenStrike(unit, targetUnit, ability)
                }
            }
            AbilityType.RICOCHET_BEAM -> {
                if (unit != null) {
                    executeRicochetSnipe(unit, targetPos, ability)
                }
            }
            AbilityType.FLUX_SIPHON -> {
                executeFluxSiphon(targetPos, ability)
            }
            else -> {}
        }
    }

    private fun executeMatrixTransformation(
        matrix: Matrix2x2,
        pivot: GridPos,
        ability: TacticalAbility
    ) {
        val state = _battleState.value
        if (state.playerEnergy < ability.energyCost || state.playerActionPoints < ability.actionPointCost) return

        val friendlyUnits = state.playerUnits
        val occupied = mutableSetOf<GridPos>()
        val updatedUnits = state.units.map { unit ->
            if (unit.faction == UnitFaction.PLAYER && unit.isAlive) {
                val newPos = MatrixMathEngine.transformAroundPivot(
                    source = unit.pos,
                    pivot = pivot,
                    matrix = matrix,
                    gridWidth = state.gridWidth,
                    gridHeight = state.gridHeight,
                    occupiedPositions = occupied
                )
                occupied.add(newPos)
                unit.copy(pos = newPos)
            } else {
                occupied.add(unit.pos)
                unit
            }
        }

        vibrate(45)
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "Executed ${ability.name} around pivot (${pivot.x}, ${pivot.y})!",
            formula = "${ability.formulaSymbol} | det=${String.format("%.1f", matrix.det)}"
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerEnergy = it.playerEnergy - ability.energyCost,
                playerActionPoints = it.playerActionPoints - ability.actionPointCost,
                selectedAbility = null,
                transformationPreview = emptyMap(),
                combatLogs = listOf(log) + it.combatLogs
            )
        }
    }

    private fun executeAttack(attacker: TacticalUnit, target: TacticalUnit) {
        val state = _battleState.value
        val (finalDamage, wasShielded) = MatrixMathEngine.calculateShieldMitigation(
            target = target,
            attackerPos = attacker.pos,
            incomingDamage = attacker.unitClass.baseDamage
        )

        val newHp = (target.hp - finalDamage).coerceAtLeast(0)
        val updatedUnits = state.units.map {
            when (it.id) {
                target.id -> it.copy(hp = newHp)
                attacker.id -> it.copy(hasActedThisTurn = true)
                else -> it
            }
        }

        vibrate(30)
        val shieldNote = if (wasShielded) " (Shield Dot-Mitigation -70%!)" else ""
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "${attacker.name} attacked ${target.name} for $finalDamage dmg$shieldNote",
            formula = "D = ${attacker.unitClass.baseDamage} - (v·n)",
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerActionPoints = (it.playerActionPoints - 1).coerceAtLeast(0),
                combatLogs = listOf(log) + it.combatLogs,
                statsDamageDealt = it.statsDamageDealt + finalDamage
            )
        }
        checkBattleEnd()
    }

    private fun executeSingularityStrike(target: TacticalUnit, ability: TacticalAbility) {
        val state = _battleState.value
        val pureDamage = 50
        val newHp = (target.hp - pureDamage).coerceAtLeast(0)

        val updatedUnits = state.units.map {
            if (it.id == target.id) it.copy(hp = newHp, isShieldActive = false) else it
        }

        vibrate(60)
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "SINGULARITY COLLAPSE: Shattered ${target.name}'s tensor shield! 50 Pure DMG!",
            formula = "det(A) = ad - bc = 0",
            isCrit = true,
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerEnergy = it.playerEnergy - ability.energyCost,
                playerActionPoints = it.playerActionPoints - ability.actionPointCost,
                selectedAbility = null,
                combatLogs = listOf(log) + it.combatLogs,
                statsSingularitiesTriggered = it.statsSingularitiesTriggered + 1,
                statsDamageDealt = it.statsDamageDealt + pureDamage
            )
        }
        checkBattleEnd()
    }

    private fun executeEigenStrike(attacker: TacticalUnit, target: TacticalUnit, ability: TacticalAbility) {
        val state = _battleState.value
        val barrier = state.eigenBarrier
        val (critMultiplier, isResonant) = if (barrier != null) {
            MatrixMathEngine.checkEigenResonance(attacker.pos, target.pos, barrier)
        } else {
            Pair(1.5f, false)
        }

        val damage = Math.round(attacker.unitClass.baseDamage * critMultiplier)
        val newHp = (target.hp - damage).coerceAtLeast(0)

        val updatedUnits = state.units.map {
            if (it.id == target.id) it.copy(hp = newHp) else it
        }

        vibrate(50)
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = if (isResonant) "EIGEN RESONANCE ALIGNED! 300% CRIT on ${target.name} for $damage dmg!"
            else "Eigen beam dealt $damage damage to ${target.name}",
            formula = "Av = λv",
            isCrit = isResonant,
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerEnergy = it.playerEnergy - ability.energyCost,
                playerActionPoints = it.playerActionPoints - ability.actionPointCost,
                selectedAbility = null,
                combatLogs = listOf(log) + it.combatLogs,
                statsEigenHits = if (isResonant) it.statsEigenHits + 1 else it.statsEigenHits,
                statsDamageDealt = it.statsDamageDealt + damage
            )
        }
        checkBattleEnd()
    }

    private fun executeRicochetSnipe(attacker: TacticalUnit, targetPos: GridPos, ability: TacticalAbility) {
        val state = _battleState.value
        val dir = GridPos(
            (targetPos.x - attacker.pos.x).coerceIn(-1, 1),
            (targetPos.y - attacker.pos.y).coerceIn(-1, 1)
        )
        val path = MatrixMathEngine.traceRicochetPath(
            start = attacker.pos,
            direction = dir,
            cells = state.cells,
            width = state.gridWidth,
            height = state.gridHeight
        )

        val pathSet = path.toSet()
        val hitEnemies = state.enemyUnits.filter { pathSet.contains(it.pos) }
        var totalDamage = 0

        val updatedUnits = state.units.map { unit ->
            if (hitEnemies.any { it.id == unit.id }) {
                val dmg = 45
                totalDamage += dmg
                unit.copy(hp = (unit.hp - dmg).coerceAtLeast(0))
            } else unit
        }

        vibrate(40)
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "Ricochet Snipe bounced through ${path.size} coordinate cells, hitting ${hitEnemies.size} enemies!",
            formula = "r = d - 2(d·n)n",
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerEnergy = it.playerEnergy - ability.energyCost,
                playerActionPoints = it.playerActionPoints - ability.actionPointCost,
                selectedAbility = null,
                ricochetPath = path,
                combatLogs = listOf(log) + it.combatLogs,
                statsDamageDealt = it.statsDamageDealt + totalDamage
            )
        }
        checkBattleEnd()
    }

    private fun executeFluxSiphon(pos: GridPos, ability: TacticalAbility) {
        val state = _battleState.value
        val cell = state.cells[pos] ?: return
        val harvestedEnergy = (cell.diffusionEnergy * 0.7f).toInt().coerceAtLeast(10)

        val updatedCells = state.cells.toMutableMap()
        updatedCells[pos] = cell.copy(diffusionEnergy = cell.diffusionEnergy * 0.3f)

        vibrate(25)
        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "Flux Siphon converted thermal diffusion field into +$harvestedEnergy Energy!",
            formula = "∂E/∂t -> ΔE",
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                cells = updatedCells,
                playerEnergy = (it.playerEnergy + harvestedEnergy).coerceAtMost(it.maxPlayerEnergy),
                playerActionPoints = (it.playerActionPoints - ability.actionPointCost).coerceAtLeast(0),
                selectedAbility = null,
                combatLogs = listOf(log) + it.combatLogs
            )
        }
    }

    private fun executeMove(unit: TacticalUnit, targetPos: GridPos, isDash: Boolean = false) {
        val state = _battleState.value
        val updatedUnits = state.units.map {
            if (it.id == unit.id) it.copy(
                pos = targetPos,
                movesRemaining = if (isDash) it.movesRemaining else (it.movesRemaining - 1).coerceAtLeast(0)
            ) else it
        }

        val log = CombatLogEntry(
            turn = state.turnNumber,
            message = "${unit.name} translated to (${targetPos.x}, ${targetPos.y})",
            formula = "p' = p + d",
            isPlayerAction = true
        )

        _battleState.update {
            it.copy(
                units = updatedUnits,
                combatLogs = listOf(log) + it.combatLogs
            )
        }
    }

    fun onActivateShield() {
        val state = _battleState.value
        val unit = state.selectedUnit ?: return
        if (state.playerEnergy < 15) return

        val closestEnemy = state.enemyUnits.minByOrNull { it.pos.manhattanDistance(unit.pos) }
        val angle = if (closestEnemy != null) {
            val dx = (closestEnemy.pos.x - unit.pos.x).toFloat()
            val dy = (closestEnemy.pos.y - unit.pos.y).toFloat()
            (Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360f) % 360f
        } else 0f

        val updatedUnits = state.units.map {
            if (it.id == unit.id) it.copy(isShieldActive = !it.isShieldActive, shieldAngleDeg = angle) else it
        }

        vibrate(25)
        _battleState.update {
            it.copy(
                units = updatedUnits,
                playerEnergy = it.playerEnergy - 15,
                combatLogs = listOf(
                    CombatLogEntry(
                        turn = it.turnNumber,
                        message = "${unit.name} deployed directional tensor shield facing ${angle.toInt()}°!",
                        formula = "cos θ = (v·n)/|v||n|"
                    )
                ) + it.combatLogs
            )
        }
    }

    fun onEndTurn() {
        val state = _battleState.value
        if (state.phase != BattlePhase.PLAYER_TURN) return

        viewModelScope.launch {
            // 1. Advance discrete diffusion PDE for battlefield thermal flux
            val nextCells = MatrixMathEngine.stepDiffusionPde(
                currentCells = state.cells,
                width = state.gridWidth,
                height = state.gridHeight
            )

            // 2. Node occupants harvest energy / take hazard
            var energyDelta = 0
            val postNodeUnits = state.units.map { unit ->
                val cell = nextCells[unit.pos]
                when (cell?.type) {
                    CellType.ENERGY_NODE -> {
                        if (unit.faction == UnitFaction.PLAYER) energyDelta += 15
                        unit.copy(energy = (unit.energy + 15).coerceAtMost(unit.maxEnergy))
                    }
                    CellType.HAZARD_ZONE -> {
                        unit.copy(hp = (unit.hp - 12).coerceAtLeast(0))
                    }
                    else -> unit
                }
            }

            _battleState.update {
                it.copy(
                    cells = nextCells,
                    units = postNodeUnits,
                    phase = BattlePhase.AI_TURN,
                    playerEnergy = (it.playerEnergy + energyDelta).coerceAtMost(it.maxPlayerEnergy),
                    combatLogs = listOf(
                        CombatLogEntry(
                            turn = it.turnNumber,
                            message = "PDE Diffusion flux expanded. Enemy AI calculating game-theory response.",
                            formula = "∂E/∂t = D ∇²E - γE"
                        )
                    ) + it.combatLogs
                )
            }

            delay(700)

            // 3. AI Turn Execution
            executeAiTurn()
        }
    }

    private suspend fun executeAiTurn() {
        var action = TacticalAiEngine.computeNextAction(_battleState.value)
        var actionsCount = 0

        while (action != null && action !is AiAction.PassTurn && actionsCount < 3) {
            actionsCount++
            when (action) {
                is AiAction.Attack -> {
                    val attacker = _battleState.value.units.find { it.id == action.attackerId }
                    val target = _battleState.value.units.find { it.id == action.targetId }
                    if (attacker != null && target != null) {
                        val (dmg, wasShielded) = MatrixMathEngine.calculateShieldMitigation(
                            target = target,
                            attackerPos = attacker.pos,
                            incomingDamage = attacker.unitClass.baseDamage
                        )
                        val updated = _battleState.value.units.map {
                            when (it.id) {
                                target.id -> it.copy(hp = (it.hp - dmg).coerceAtLeast(0))
                                attacker.id -> it.copy(hasActedThisTurn = true)
                                else -> it
                            }
                        }
                        val shieldMsg = if (wasShielded) " (Shield Mitigated -70%!)" else ""
                        _battleState.update {
                            it.copy(
                                units = updated,
                                combatLogs = listOf(
                                    CombatLogEntry(
                                        turn = it.turnNumber,
                                        message = "AI ${attacker.name} attacked ${target.name} for $dmg dmg$shieldMsg",
                                        isPlayerAction = false
                                    )
                                ) + it.combatLogs
                            )
                        }
                    }
                }
                is AiAction.Move -> {
                    val unit = _battleState.value.units.find { it.id == action.unitId }
                    if (unit != null) {
                        val updated = _battleState.value.units.map {
                            if (it.id == unit.id) it.copy(pos = action.targetPos, hasActedThisTurn = true) else it
                        }
                        _battleState.update { it.copy(units = updated) }
                    }
                }
                is AiAction.ActivateShield -> {
                    val updated = _battleState.value.units.map {
                        if (it.id == action.unitId) it.copy(isShieldActive = true, shieldAngleDeg = action.angleDeg, hasActedThisTurn = true) else it
                    }
                    _battleState.update { it.copy(units = updated) }
                }
                else -> {}
            }
            delay(500)
            action = TacticalAiEngine.computeNextAction(_battleState.value)
        }

        // Reset units for next player turn
        val newUnits = _battleState.value.units.map {
            it.copy(hasActedThisTurn = false, movesRemaining = 1)
        }

        _battleState.update {
            it.copy(
                units = newUnits,
                turnNumber = it.turnNumber + 1,
                phase = BattlePhase.PLAYER_TURN,
                playerActionPoints = it.maxActionPoints,
                playerEnergy = (it.playerEnergy + 20).coerceAtMost(it.maxPlayerEnergy)
            )
        }
        checkBattleEnd()
    }

    private fun checkBattleEnd() {
        val state = _battleState.value
        val playerAlive = state.playerUnits.isNotEmpty()
        val enemyAlive = state.enemyUnits.isNotEmpty()

        if (!enemyAlive) {
            _battleState.update { it.copy(phase = BattlePhase.VICTORY) }
            val score = (state.statsDamageDealt * 5) + (state.statsSingularitiesTriggered * 150) + (state.statsEigenHits * 200) - (state.turnNumber * 30)
            viewModelScope.launch {
                repository.recordVictory(
                    sectorNumber = state.sectorId,
                    score = score.coerceAtLeast(600),
                    singularitiesTriggered = state.statsSingularitiesTriggered,
                    eigenCrits = state.statsEigenHits
                )
            }
        } else if (!playerAlive) {
            _battleState.update { it.copy(phase = BattlePhase.DEFEAT) }
        }
    }

    fun deselectUnit() {
        _battleState.update {
            it.copy(
                selectedUnitId = null,
                selectedAbility = null,
                transformationPreview = emptyMap(),
                ricochetPath = emptyList()
            )
        }
    }

    private fun consumeResources(energy: Int, ap: Int) {
        _battleState.update {
            it.copy(
                playerEnergy = (it.playerEnergy - energy).coerceAtLeast(0),
                playerActionPoints = (it.playerActionPoints - ap).coerceAtLeast(0)
            )
        }
    }

    private fun clearAbilitySelection() {
        _battleState.update {
            it.copy(
                selectedAbility = null,
                transformationPreview = emptyMap(),
                ricochetPath = emptyList()
            )
        }
    }

    private fun vibrate(durationMs: Long) {
        if (context == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) { }
    }
}
