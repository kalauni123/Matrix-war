package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val rankTitle: String = "Vector Cadet",
    val rankLevel: Int = 1,
    val credits: Int = 500,
    val energyCores: Int = 5,
    val highestSectorUnlocked: Int = 1,
    val totalVictories: Int = 0,
    val totalSingularitiesTriggered: Int = 0,
    val totalEigenCrits: Int = 0,
    val totalTransformationsCast: Int = 0,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)
