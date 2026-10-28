package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tech_upgrades")
data class TechUpgradeEntity(
    @PrimaryKey val techId: String,
    val name: String,
    val category: String, // MATRIX, UNIT, PASSIVE
    val description: String,
    val mathFormula: String,
    val currentLevel: Int = 0,
    val maxLevel: Int = 5,
    val baseCreditCost: Int = 150
) {
    val upgradeCost: Int
        get() = baseCreditCost * (currentLevel + 1)

    val isMaxed: Boolean
        get() = currentLevel >= maxLevel
}
