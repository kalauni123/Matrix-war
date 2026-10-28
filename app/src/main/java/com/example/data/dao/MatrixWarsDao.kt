package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.PlayerProfileEntity
import com.example.data.entity.SectorEntity
import com.example.data.entity.TechUpgradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatrixWarsDao {

    // Player Profile
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    // Sectors
    @Query("SELECT * FROM sectors ORDER BY sectorNumber ASC")
    fun getAllSectors(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM sectors WHERE sectorNumber = :number")
    suspend fun getSector(number: Int): SectorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSectors(sectors: List<SectorEntity>)

    @Update
    suspend fun updateSector(sector: SectorEntity)

    // Tech Upgrades
    @Query("SELECT * FROM tech_upgrades")
    fun getAllTechUpgrades(): Flow<List<TechUpgradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechUpgrades(upgrades: List<TechUpgradeEntity>)

    @Update
    suspend fun updateTechUpgrade(upgrade: TechUpgradeEntity)
}
