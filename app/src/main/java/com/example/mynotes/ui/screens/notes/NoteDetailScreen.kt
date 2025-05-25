package com.example.mynotes.ui.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.data.models.Note
import com.example.mynotes.ui.components.RichTextEditor
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    noteId: Int
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var formattedContent by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Black) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var scheduledDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            viewModel.allNotes.collect { notes ->
                notes.find { it.id == noteId }?.let { note ->
                    title = note.title
                    content = note.content
                    formattedContent = note.formattedContent
                    textColor = Color(note.textColor)
                    backgroundColor = Color(note.backgroundColor)
                    note.scheduledDate?.let {
                        val instant = Instant.fromEpochMilliseconds(it)
                        scheduledDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        scheduledDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { Text(stringResource(R.string.choose_date)) },
                showModeToggle = false
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1) stringResource(R.string.new_note) else stringResource(R.string.edit_note)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, stringResource(R.string.schedule))
                    }
                    IconButton(onClick = {
                        viewModel.insert(
                            Note(
                                id = if (noteId == -1) 0 else noteId,
                                title = title,
                                content = content,
                                formattedContent = formattedContent,
                                textColor = textColor.value.toInt(),
                                backgroundColor = backgroundColor.value.toInt(),
                                scheduledDate = scheduledDate?.let {
                                    it.atTime(0, 0)
                                        .toInstant(TimeZone.currentSystemDefault())
                                        .toEpochMilliseconds()
                                }
                            )
                        )
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Save, stringResource(R.string.save))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RichTextEditor(
                value = content,
                onValueChange = { content = it },
                onFormattedValueChange = { formattedContent = it },
                textColor = textColor,
                onTextColorChange = { textColor = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            scheduledDate?.let { date ->
                AssistChip(
                    onClick = { showDatePicker = true },
                    label = { 
                        Text(
                            stringResource(
                                R.string.scheduled_for,
                                "${date.dayOfMonth.toString().padStart(2, '0')}.${date.monthNumber.toString().padStart(2, '0')}.${date.year}"
                            )
                        )
                    }
                )
            }
        }
    }
}