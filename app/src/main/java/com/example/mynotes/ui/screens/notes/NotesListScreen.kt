package com.example.mynotes.ui.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.data.models.Note
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import com.example.mynotes.ui.navigation.Screen
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, stringResource(R.string.settings))
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Calendar.route) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, stringResource(R.string.calendar))
                }
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.NoteDetail.createRoute(-1)) }
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_note))
                }
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_notes))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigate(Screen.NoteDetail.createRoute(note.id)) }
                    )
                }
            }
        }
    }
}

fun parseFormattedText(text: String): AnnotatedString {
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

@Composable
private fun NoteItem(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(note.textColor),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = note.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(note.textColor).copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = parseFormattedText(note.content.take(100) + if (note.content.length > 100) "..." else ""),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(note.textColor)
            )
            if (note.scheduledDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.scheduled_for, note.getFormattedDate()),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(note.textColor).copy(alpha = 0.6f)
                )
            }
        }
    }
}