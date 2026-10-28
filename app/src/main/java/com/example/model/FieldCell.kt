package com.example.model

enum class CellType(val displayName: String, val description: String) {
    EMPTY("Empty Grid", "Standard coordinate cell."),
    ENERGY_NODE("Matrix Node", "+15 Energy per turn to occupant."),
    DEFENSE_NODE("Shield Matrix", "Provides 40% directional damage mitigation."),
    TRANSFORMATION_ZONE("Quantum Warp", "Reduces Matrix Transformation AP cost by 50%."),
    HAZARD_ZONE("Entropy Void", "Deals 15 damage at start of turn."),
    FLUX_REACTOR("PDE Reactor", "Diffusion source continuously spreading thermal energy."),
    MIRROR_WALL("Reflective Barrier", "Ricochets vector sniper beams at 90-degree angle.")
}

/**
 * Grid cell with tactical and PDE diffusion properties
 */
data class FieldCell(
    val pos: GridPos,
    val type: CellType = CellType.EMPTY,
    val diffusionEnergy: Float = 0f, // Discrete PDE field value: 0..100
    val isOccupied: Boolean = false,
    val occupantUnitId: String? = null
) {
    val heatIntensity: Float
        get() = (diffusionEnergy / 100f).coerceIn(0f, 1f)
}
