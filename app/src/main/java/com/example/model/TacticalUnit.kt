package com.example.model

enum class UnitFaction {
    PLAYER,
    ENEMY_AI,
    NEUTRAL
}

enum class UnitClass(
    val title: String,
    val description: String,
    val baseHp: Int,
    val baseEnergy: Int,
    val attackRange: Int,
    val baseDamage: Int,
    val moveRange: Int
) {
    COMMANDER(
        title = "Matrix Commander",
        description = "Core node anchor. Powers team transformations and matrix locks.",
        baseHp = 120,
        baseEnergy = 100,
        attackRange = 3,
        baseDamage = 28,
        moveRange = 2
    ),
    STRIKER(
        title = "Vector Striker",
        description = "High-velocity frontline unit capable of Vector Dash translation jumps.",
        baseHp = 80,
        baseEnergy = 60,
        attackRange = 2,
        baseDamage = 35,
        moveRange = 3
    ),
    DEFENDER(
        title = "Geometric Defender",
        description = "Heavily armored fortress with directional dot-product shield matrix.",
        baseHp = 140,
        baseEnergy = 50,
        attackRange = 1,
        baseDamage = 22,
        moveRange = 2
    ),
    SNIPER(
        title = "Vector Sniper",
        description = "Fires long-range focused geometric beams with ricochet reflection.",
        baseHp = 65,
        baseEnergy = 70,
        attackRange = 6,
        baseDamage = 40,
        moveRange = 2
    ),
    SINGULARITY_CORE(
        title = "Singularity Disruptor",
        description = "Manipulates determinants. Can trigger zero-determinant collapses.",
        baseHp = 75,
        baseEnergy = 90,
        attackRange = 4,
        baseDamage = 30,
        moveRange = 2
    )
}

data class TacticalUnit(
    val id: String,
    val name: String,
    val faction: UnitFaction,
    val unitClass: UnitClass,
    val pos: GridPos,
    val hp: Int,
    val maxHp: Int,
    val energy: Int,
    val maxEnergy: Int,
    val shieldAngleDeg: Float = 0f, // Direction of active shield normal
    val isShieldActive: Boolean = false,
    val isOverclocked: Boolean = false,
    val hasActedThisTurn: Boolean = false,
    val movesRemaining: Int = 1
) {
    val isAlive: Boolean
        get() = hp > 0

    val hpPercent: Float
        get() = if (maxHp > 0) (hp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f) else 0f

    val energyPercent: Float
        get() = if (maxEnergy > 0) (energy.toFloat() / maxEnergy.toFloat()).coerceIn(0f, 1f) else 0f
}
