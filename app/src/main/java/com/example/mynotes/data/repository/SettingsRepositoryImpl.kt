package com.example.mynotes.data.repository

import com.example.mynotes.data.database.dao.SettingsDao
import com.example.mynotes.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    private val appSettings: Flow<AppSettings> = settingsDao.getSettings()

    override fun getSettings(): Flow<AppSettings> = appSettings

    override suspend fun updateSettings(settings: AppSettings) {
        settingsDao.update(settings)
    }
} 