package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectors")
data class SectorEntity(
    @PrimaryKey val sectorNumber: Int,
    val title: String,
    val subtitle: String,
    val mathConcept: String,
    val formulaDisplay: String,
    val briefing: String,
    val enemyCommanderName: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val starsEarned: Int = 0,
    val bestScore: Int = 0
)
