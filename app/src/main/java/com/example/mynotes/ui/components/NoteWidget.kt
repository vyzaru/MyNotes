package com.example.mynotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.models.Note
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString

@Composable
fun NoteWidget(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(note.backgroundColor)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = parseFormattedText(note.content.take(100) + if (note.content.length > 100) "..." else ""),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun parseFormattedText(text: String): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        var isInBold = false
        var isInItalic = false
        
        while (currentIndex < text.length) {
            when {
                text.substring(currentIndex).startsWith("**") -> {
                    isInBold = !isInBold
                    currentIndex += 2
                }
                text.substring(currentIndex).startsWith("_") -> {
                    isInItalic = !isInItalic
                    currentIndex += 1
                }
                text.substring(currentIndex).startsWith("• ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("• ")
                    }
                    currentIndex += 2
                }
                else -> {
                    val style = SpanStyle(
                        fontWeight = if (isInBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (isInItalic) FontStyle.Italic else FontStyle.Normal
                    )
                    withStyle(style) {
                        append(text[currentIndex])
                    }
                    currentIndex++
                }
            }
        }
    }
}