package com.example.mynotes.ui.screens.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.models.AppSettings
import com.example.mynotes.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<AppSettings> = repository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun toggleDarkTheme(isDark: Boolean) {
        Log.d("SettingsViewModel", "Toggling dark theme to: $isDark")
        viewModelScope.launch {
            try {
                repository.updateSettings(settings.value.copy(isDarkTheme = isDark))
                Log.d("SettingsViewModel", "Dark theme updated successfully")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating dark theme", e)
            }
        }
    }

    fun updateFont(font: String) {
        Log.d("SettingsViewModel", "Updating font to: $font")
        viewModelScope.launch {
            try {
                repository.updateSettings(settings.value.copy(selectedFontFamily = font))
                Log.d("SettingsViewModel", "Font updated successfully")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating font", e)
            }
        }
    }

    fun updateFontSize(size: Float) {
        Log.d("SettingsViewModel", "Updating font size to: $size")
        viewModelScope.launch {
            try {
                repository.updateSettings(settings.value.copy(fontSize = size))
                Log.d("SettingsViewModel", "Font size updated successfully")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating font size", e)
            }
        }
    }
}