package com.example.model

enum class AbilityType {
    SPATIAL_SHIFT,      // Matrix Rotation 90°
    MIRROR_STRIKE,      // Matrix Reflection across axis
    VECTOR_DASH,        // Translation jump along vector
    SCALE_SURGE,        // Matrix Scaling 2x burst
    SINGULARITY_STRIKE, // det(A) = 0 collapse against shielded targets
    EIGEN_WEAPON,       // Resonant beam along eigenvector of enemy barrier
    RICOCHET_BEAM,      // Reflected vector snipe
    FLUX_SIPHON         // Harvests PDE diffusion heat for team energy & shields
}

data class TacticalAbility(
    val type: AbilityType,
    val name: String,
    val formulaSymbol: String,
    val description: String,
    val energyCost: Int,
    val actionPointCost: Int,
    val matrixRep: Matrix2x2? = null,
    val iconName: String
) {
    companion object {
        val ALL_ABILITIES = listOf(
            TacticalAbility(
                type = AbilityType.SPATIAL_SHIFT,
                name = "Spatial Shift",
                formulaSymbol = "R(90°)",
                description = "Rotate friendly or hostile units 90° clockwise around a selected pivot point.",
                energyCost = 30,
                actionPointCost = 1,
                matrixRep = Matrix2x2.ROTATION_90,
                iconName = "rotate_right"
            ),
            TacticalAbility(
                type = AbilityType.MIRROR_STRIKE,
                name = "Mirror Strike",
                formulaSymbol = "F(y)",
                description = "Reflect formation across the horizontal or vertical tactical axis.",
                energyCost = 25,
                actionPointCost = 1,
                matrixRep = Matrix2x2.REFLECTION_HORIZONTAL,
                iconName = "flip"
            ),
            TacticalAbility(
                type = AbilityType.VECTOR_DASH,
                name = "Vector Dash",
                formulaSymbol = "p + d",
                description = "Instantly translate a unit up to 3 cells along a straight coordinate vector.",
                energyCost = 20,
                actionPointCost = 1,
                iconName = "arrow_forward"
            ),
            TacticalAbility(
                type = AbilityType.SINGULARITY_STRIKE,
                name = "Singularity Strike",
                formulaSymbol = "det(A) = 0",
                description = "Collapses the target's 2D defensive tensor into a line. Shatters all barriers and deals 45 pure damage.",
                energyCost = 45,
                actionPointCost = 2,
                matrixRep = Matrix2x2.SINGULARITY_PROJECTOR,
                iconName = "blur_circular"
            ),
            TacticalAbility(
                type = AbilityType.EIGEN_WEAPON,
                name = "Eigen Resonance",
                formulaSymbol = "Av = λv",
                description = "Fires a resonant beam aligned with the target's dominant eigenvector for 300% critical damage.",
                energyCost = 40,
                actionPointCost = 2,
                iconName = "flash_on"
            ),
            TacticalAbility(
                type = AbilityType.RICOCHET_BEAM,
                name = "Ricochet Snipe",
                formulaSymbol = "r = d - 2(d·n)n",
                description = "Fires high-velocity vector laser bouncing off mirror walls to strike targets behind obstacles.",
                energyCost = 35,
                actionPointCost = 1,
                iconName = "timeline"
            ),
            TacticalAbility(
                type = AbilityType.FLUX_SIPHON,
                name = "Flux Siphon",
                formulaSymbol = "∂E/∂t",
                description = "Harvests continuous PDE field energy from current and adjacent cells, restoring HP and Energy.",
                energyCost = 15,
                actionPointCost = 1,
                iconName = "waves"
            )
        )
    }
}
