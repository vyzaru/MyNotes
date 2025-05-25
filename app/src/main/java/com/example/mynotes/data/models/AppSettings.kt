package com.example.mynotes.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 0,
    val isDarkTheme: Boolean = false,
    val selectedFontFamily: String = "Roboto",
    val fontSize: Float = 16f
)