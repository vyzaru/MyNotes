package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun FontSettings(
    currentFont: String,
    onFontSelected: (String) -> Unit,
    currentSize: Float,
    onSizeChanged: (Float) -> Unit
) {
    Column {
        Text("Шрифт", style = MaterialTheme.typography.titleMedium)
        Row {
            listOf("sans-serif", "serif").forEach { font ->
                FilterChip(
                    selected = currentFont == font,
                    onClick = { onFontSelected(font) },
                    label = { Text(font) }
                )
            }
        }

        Text("Размер шрифта: ${currentSize.toInt()}sp")
        Slider(
            value = currentSize,
            onValueChange = onSizeChanged,
            valueRange = 12f..24f
        )
    }
}