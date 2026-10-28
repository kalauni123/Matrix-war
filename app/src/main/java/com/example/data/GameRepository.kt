package com.example.data

import com.example.data.dao.MatrixWarsDao
import com.example.data.entity.PlayerProfileEntity
import com.example.data.entity.SectorEntity
import com.example.data.entity.TechUpgradeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val dao: MatrixWarsDao) {

    val playerProfile: Flow<PlayerProfileEntity?> = dao.getPlayerProfile()
    val allSectors: Flow<List<SectorEntity>> = dao.getAllSectors()
    val allTechUpgrades: Flow<List<TechUpgradeEntity>> = dao.getAllTechUpgrades()

    suspend fun initializeDefaultsIfNeeded() {
        // Initialize Player Profile
        val currentProfile = dao.getPlayerProfile().firstOrNull()
        if (currentProfile == null) {
            dao.insertOrUpdateProfile(PlayerProfileEntity())
        }

        // Initialize Sectors if empty
        val currentSectors = dao.getAllSectors().firstOrNull()
        if (currentSectors.isNullOrEmpty()) {
            dao.insertSectors(DEFAULT_SECTORS)
        }

        // Initialize Tech Upgrades if empty
        val currentTech = dao.getAllTechUpgrades().firstOrNull()
        if (currentTech.isNullOrEmpty()) {
            dao.insertTechUpgrades(DEFAULT_TECH)
        }
    }

    suspend fun recordVictory(
        sectorNumber: Int,
        score: Int,
        singularitiesTriggered: Int,
        eigenCrits: Int
    ) {
        val sector = dao.getSector(sectorNumber) ?: return
        val stars = when {
            score >= 1200 -> 3
            score >= 800 -> 2
            else -> 1
        }
        val updatedSector = sector.copy(
            isCompleted = true,
            starsEarned = maxOf(sector.starsEarned, stars),
            bestScore = maxOf(sector.bestScore, score)
        )
        dao.updateSector(updatedSector)

        // Unlock next sector
        val nextSector = dao.getSector(sectorNumber + 1)
        if (nextSector != null && !nextSector.isUnlocked) {
            dao.updateSector(nextSector.copy(isUnlocked = true))
        }

        // Update player profile stats & grant rewards
        val profile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        val earnedCredits = 250 + (stars * 75)
        val earnedCores = stars

        val updatedProfile = profile.copy(
            credits = profile.credits + earnedCredits,
            energyCores = profile.energyCores + earnedCores,
            highestSectorUnlocked = maxOf(profile.highestSectorUnlocked, sectorNumber + 1),
            totalVictories = profile.totalVictories + 1,
            totalSingularitiesTriggered = profile.totalSingularitiesTriggered + singularitiesTriggered,
            totalEigenCrits = profile.totalEigenCrits + eigenCrits,
            rankLevel = calculateRankLevel(profile.totalVictories + 1),
            rankTitle = calculateRankTitle(profile.totalVictories + 1)
        )
        dao.insertOrUpdateProfile(updatedProfile)
    }

    suspend fun purchaseUpgrade(techId: String): Boolean {
        val upgrades = dao.getAllTechUpgrades().firstOrNull() ?: return false
        val tech = upgrades.find { it.techId == techId } ?: return false
        val profile = dao.getPlayerProfile().firstOrNull() ?: return false

        if (tech.isMaxed || profile.credits < tech.upgradeCost) {
            return false
        }

        val newCredits = profile.credits - tech.upgradeCost
        dao.insertOrUpdateProfile(profile.copy(credits = newCredits))
        dao.updateTechUpgrade(tech.copy(currentLevel = tech.currentLevel + 1))
        return true
    }

    suspend fun updateSettings(haptics: Boolean, sound: Boolean) {
        val profile = dao.getPlayerProfile().firstOrNull() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(profile.copy(hapticsEnabled = haptics, soundEnabled = sound))
    }

    private fun calculateRankLevel(victories: Int): Int = (victories / 2) + 1

    private fun calculateRankTitle(victories: Int): String = when {
        victories >= 20 -> "Quantum Field Marshal"
        victories >= 15 -> "Eigenvalue Sovereign"
        victories >= 10 -> "Tensor General"
        victories >= 6 -> "Singularity Strategist"
        victories >= 3 -> "Vector Tactician"
        else -> "Matrix Apprentice"
    }

    companion object {
        val DEFAULT_SECTORS = listOf(
            SectorEntity(
                sectorNumber = 1,
                title = "Sector 1: Coordinate Zero",
                subtitle = "Linear Transformations & Vector Movement",
                mathConcept = "2D Coordinate Vectors p = [x, y]^T",
                formulaDisplay = "p' = p + d",
                briefing = "Hostile reconnaissance units have breached Grid Sector 0. Utilize Vector Dashes and flanking maneuvers to neutralize their forward commander.",
                enemyCommanderName = "Unit Alpha-0",
                isUnlocked = true
            ),
            SectorEntity(
                sectorNumber = 2,
                title = "Sector 2: Spatial Rotations",
                subtitle = "Clockwise Matrix Rotation & Tactical Shifting",
                mathConcept = "Rotation Matrix R(90°) = [0, -1; 1, 0]",
                formulaDisplay = "p' = R(90°)(p - c) + c",
                briefing = "Enemy defenders have established an impenetrable front line. Execute Spatial Shifts around central pivots to swing into their unshielded flank.",
                enemyCommanderName = "Orthogonal-X",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 3,
                title = "Sector 3: The Singularity Threshold",
                subtitle = "Determinants & Structural Collapse",
                mathConcept = "det(A) = ad - bc = 0",
                formulaDisplay = "det(A) = 0 => Tensor Collapse",
                briefing = "The enemy commander is shielded by a 2D tension tensor. Activate Singularity Strikes to drive the matrix determinant to zero and shatter their armor.",
                enemyCommanderName = "Singular Guardian",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 4,
                title = "Sector 4: Eigen Harmonic Core",
                subtitle = "Eigenvectors & Critical Resonance",
                mathConcept = "Characteristic Equation Av = λv",
                formulaDisplay = "A v = λ v (Resonance 300% Crit)",
                briefing = "A massive rotating barrier absorbs standard kinetic rounds. Identify the stable eigenvector direction and channel the Eigenweapon directly along it.",
                enemyCommanderName = "Resonance Prime",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 5,
                title = "Sector 5: PDE Thermal Flux",
                subtitle = "Continuous Field Diffusion Dynamics",
                mathConcept = "∂E/∂t = D ∇²E - γE + S",
                formulaDisplay = "∇²E = (E_adj - 4E_0)",
                briefing = "Unstable Flux Reactors are diffusing extreme heat across coordinate lanes. Maneuver through safe gradients and use Flux Siphon to turn thermal energy against them.",
                enemyCommanderName = "Flux Inversion Core",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 6,
                title = "Sector 6: Mirror Reflection Trajectory",
                subtitle = "Computational Geometry & Specular Ricochets",
                mathConcept = "Specular Reflection r = d - 2(d·n)n",
                formulaDisplay = "r = d - 2(d·n)n",
                briefing = "Corridor fortified with reflective mirror barriers. Fire high-energy Vector Beams off walls to strike targets concealed behind reinforced bunkers.",
                enemyCommanderName = "Specular Mirage",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 7,
                title = "Sector 7: Noncommutative Tactics",
                subtitle = "Matrix Multiplication & Order of Operations",
                mathConcept = "AB ≠ BA (Noncommutativity)",
                formulaDisplay = "T = F · R ≠ R · F",
                briefing = "Sequence matters! Combining Spatial Rotation and Mirror Reflection in different orders produces radically different tactical outcomes. Discover the winning permutation.",
                enemyCommanderName = "Permutator 9",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 8,
                title = "Sector 8: Directional Dot Shields",
                subtitle = "Inner Products & Angular Mitigation",
                mathConcept = "Inner Product v · n = ||v|| ||n|| cos θ",
                formulaDisplay = "cos θ = (v · n) / (|v| |n|)",
                briefing = "Heavy Sentinel tanks equipped with frontal wedge shields. Flank around their normal vector to bypass their 70% damage reduction.",
                enemyCommanderName = "Aegis Vertex",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 9,
                title = "Sector 9: Convex Hull Containment",
                subtitle = "Spatial Enclosure Optimization",
                mathConcept = "Convex Combination ∑ λ_i p_i",
                formulaDisplay = "Hull(S) = { ∑ λ_i x_i | λ_i ≥ 0, ∑ λ_i = 1 }",
                briefing = "Enclose enemy strikers within a convex perimeter of friendly units to trigger systemic energy drain and pin them down.",
                enemyCommanderName = "Simplex Vanguard",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 10,
                title = "Sector 10: Stochastic Quantum Cloud",
                subtitle = "Probability Distributions & Expected Value",
                mathConcept = "Expected Value E[X] = ∑ x P(x)",
                formulaDisplay = "E[Dmg] = μ ± σ",
                briefing = "Atmospheric quantum interference introduces probabilistic damage dispersion. Optimize expected value choices to guarantee critical outcomes.",
                enemyCommanderName = "Stochastic Entity",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 11,
                title = "Sector 11: Game Theory Equilibrium",
                subtitle = "Minimax Anticipation & Nash Balance",
                mathConcept = "Minimax: min_a max_b U(a, b)",
                formulaDisplay = "max_i min_j A_ij = min_j max_i A_ij",
                briefing = "The advanced AI anticipates your obvious maneuvers. Adopt mixed tactical strategies to breach their Minimax equilibrium defenses.",
                enemyCommanderName = "Nash Overseer",
                isUnlocked = false
            ),
            SectorEntity(
                sectorNumber = 12,
                title = "Sector 12: The Omega Matrix Core",
                subtitle = "Mastery of all 10 Mathematical Principles",
                mathConcept = "Unified Field Tensor: det, eig, PDE, ricochet",
                formulaDisplay = "Ω(t) = exp(At) v_0 + ∫ exp(A(t-s)) S(s) ds",
                briefing = "Final confrontation at the Matrix Core. Omega rotates the battlefield, projects resonant eigenbarriers, and diffuses lethal thermal storms. Unleash your full tactical mastery!",
                enemyCommanderName = "Omega Matrix Core",
                isUnlocked = false
            )
        )

        val DEFAULT_TECH = listOf(
            TechUpgradeEntity(
                techId = "tech_matrix_ap",
                name = "Subspace Capacitors",
                category = "MATRIX",
                description = "Increases maximum player energy reserve and start-of-turn AP replenishment.",
                mathFormula = "E_max = 100 + 15·L",
                currentLevel = 0,
                baseCreditCost = 150
            ),
            TechUpgradeEntity(
                techId = "tech_eigen_amp",
                name = "Eigen Resonance Tuner",
                category = "MATRIX",
                description = "Widens acceptable angular tolerance for Eigen Resonance critical strikes.",
                mathFormula = "Δθ_crit = 30° + 5°·L",
                currentLevel = 0,
                baseCreditCost = 200
            ),
            TechUpgradeEntity(
                techId = "tech_singularity_potency",
                name = "Determinant Void Injector",
                category = "MATRIX",
                description = "Singularity strikes deal extra pure damage when collapsing enemy tensors.",
                mathFormula = "Dmg_sing = 45 + 10·L",
                currentLevel = 0,
                baseCreditCost = 250
            ),
            TechUpgradeEntity(
                techId = "tech_pde_insulation",
                name = "Thermal Field Dampener",
                category = "PASSIVE",
                description = "Friendly units take 50% less damage from hazard cells and high-temperature PDE zones.",
                mathFormula = "γ_absorb = 1.0 - 0.12·L",
                currentLevel = 0,
                baseCreditCost = 180
            ),
            TechUpgradeEntity(
                techId = "tech_ricochet_focus",
                name = "Collimated Laser Optics",
                category = "UNIT",
                description = "Vector Sniper projectiles gain +2 reflection bounces and +15% damage per ricochet.",
                mathFormula = "Dmg_ric = D_0 · (1 + 0.15·k)",
                currentLevel = 0,
                baseCreditCost = 220
            ),
            TechUpgradeEntity(
                techId = "tech_shield_angle",
                name = "Tensor Barrier Array",
                category = "UNIT",
                description = "Enhances Geometric Defender's directional shield arc and frontal damage mitigation.",
                mathFormula = "Mitigation = 70% + 4%·L",
                currentLevel = 0,
                baseCreditCost = 200
            )
        )
    }
}
