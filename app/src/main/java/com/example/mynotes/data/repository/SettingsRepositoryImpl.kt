package com.example.mynotes.data.repository

import android.util.Log
import com.example.mynotes.data.database.dao.SettingsDao
import com.example.mynotes.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    private val appSettings: Flow<AppSettings> = settingsDao.getSettings()
        .map { it ?: AppSettings() }

    override fun getSettings(): Flow<AppSettings> = appSettings

    override suspend fun updateSettings(settings: AppSettings) {
        try {
            Log.d("SettingsRepository", "Attempting to update settings: $settings")
            val count = settingsDao.getSettingsCount()
            if (count == 0) {
                Log.d("SettingsRepository", "No settings found, inserting new settings")
                settingsDao.insert(settings)
            } else {
                Log.d("SettingsRepository", "Updating existing settings")
                settingsDao.update(settings)
            }
            Log.d("SettingsRepository", "Settings updated successfully")
        } catch (e: Exception) {
            Log.e("SettingsRepository", "Error updating settings", e)
            // В случае ошибки, пробуем удалить и создать заново
            try {
                settingsDao.deleteSettings()
                settingsDao.insert(settings)
                Log.d("SettingsRepository", "Settings recreated successfully")
            } catch (e2: Exception) {
                Log.e("SettingsRepository", "Error recreating settings", e2)
                throw e2
            }
        }
    }
} 