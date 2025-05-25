package com.example.mynotes.ui.screens.settings.viewmodel

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
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(isDarkTheme = isDark))
        }
    }

    fun updateFont(font: String) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(selectedFontFamily = font))
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(fontSize = size))
        }
    }
}