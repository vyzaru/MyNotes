package com.example.mynotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.mynotes.data.models.AppSettings

@Composable
fun MyNotesTheme(
    settings: AppSettings,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    
    val colorScheme = when {
        settings.isDarkTheme -> darkColorScheme()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val dynamicColorScheme = if (settings.isDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            dynamicColorScheme
        }
        else -> lightColorScheme()
    }

    val fontFamily = when (settings.selectedFontFamily) {
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.Default
    }

    val typography = MaterialTheme.typography.copy(
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontSize = settings.fontSize.sp,
            fontFamily = fontFamily
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontSize = (settings.fontSize - 2).sp,
            fontFamily = fontFamily
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontSize = (settings.fontSize + 4).sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontSize = (settings.fontSize + 2).sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        )
    )

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !settings.isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
} 