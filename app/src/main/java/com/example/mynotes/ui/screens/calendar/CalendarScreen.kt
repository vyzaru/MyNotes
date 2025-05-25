package com.example.mynotes.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.data.models.Note
import com.example.mynotes.ui.components.CalendarWidget
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import androidx.compose.foundation.clickable
import com.example.mynotes.ui.navigation.Screen
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(
                                Screen.NoteDetail.createRoute(
                                    noteId = -1,
                                    date = selectedDate
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, stringResource(R.string.add_note))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CalendarWidget(
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(
                    R.string.notes_for_date,
                    Instant.fromEpochMilliseconds(selectedDate)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date.let { "${it.dayOfMonth}.${it.monthNumber}.${it.year}" }
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dailyNotes = notes.filter { note ->
                val noteDateTime = Instant.fromEpochMilliseconds(note.scheduledDate ?: note.createdAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                
                val selectedLocalDate = Instant.fromEpochMilliseconds(selectedDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                
                noteDateTime.date == selectedLocalDate
            }

            if (dailyNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_notes_for_date))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = dailyNotes,
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
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(note.textColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(note.textColor)
            )
        }
    }
}