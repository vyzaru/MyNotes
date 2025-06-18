package com.example.mynotes.data.database.dao

import androidx.room.*
import com.example.mynotes.data.models.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 0")
    fun getSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: AppSettings)

    @Update
    suspend fun update(settings: AppSettings)

    @Query("SELECT COUNT(*) FROM app_settings WHERE id = 0")
    suspend fun getSettingsCount(): Int

    @Query("DELETE FROM app_settings WHERE id = 0")
    suspend fun deleteSettings()
}