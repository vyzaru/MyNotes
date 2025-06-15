package com.example.mynotes.ui.screens.notes

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.data.models.Note
import com.example.mynotes.ui.components.RichTextEditor
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import com.example.mynotes.ui.screens.settings.viewmodel.SettingsViewModel
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import kotlinx.datetime.*
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
    noteId: Int,
    initialDate: Long? = null
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color(0xFF000000)) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var scheduledDate by remember { 
        mutableStateOf<LocalDate?>(
            initialDate?.let {
                Instant.fromEpochMilliseconds(it)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            }
        ) 
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = scheduledDate?.let {
            it.atTime(0, 0)
                .toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        }
    )

    LaunchedEffect(Unit) {
        if (noteId != -1) {
            viewModel.getNoteById(noteId).collect { note ->
                note?.let {
                    title = it.title
                    content = it.content
                    textColor = Color(it.textColor)
                    backgroundColor = Color(it.backgroundColor)
                    it.scheduledDate?.let { date ->
                        val instant = Instant.fromEpochMilliseconds(date)
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_note)) },
            text = { Text(stringResource(R.string.delete_note_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val noteToDelete = Note(
                            id = noteId,
                            title = title,
                            content = content,
                            formattedContent = content,
                            textColor = textColor.toArgb(),
                            backgroundColor = backgroundColor.toArgb(),
                            scheduledDate = scheduledDate?.let {
                                it.atTime(0, 0)
                                    .toInstant(TimeZone.currentSystemDefault())
                                    .toEpochMilliseconds()
                            }
                        )
                        viewModel.delete(noteToDelete)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
                    if (noteId != -1) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, stringResource(R.string.delete))
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, stringResource(R.string.schedule))
                    }
                    IconButton(onClick = {
                        val note = Note(
                            id = if (noteId == -1) 0 else noteId,
                            title = title,
                            content = content,
                            formattedContent = content,
                            textColor = textColor.toArgb(),
                            backgroundColor = backgroundColor.toArgb(),
                            scheduledDate = scheduledDate?.let {
                                it.atTime(0, 0)
                                    .toInstant(TimeZone.currentSystemDefault())
                                    .toEpochMilliseconds()
                            }
                        )
                        android.util.Log.d("NoteDetailScreen", "Saving note: $note")
                        if (noteId == -1) {
                            viewModel.insert(note)
                        } else {
                            viewModel.update(note)
                        }
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
                .background(backgroundColor)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RichTextEditor(
                value = content,
                onValueChange = { content = it },
                onFormattedValueChange = { content = it },
                textColor = textColor,
                onTextColorChange = { textColor = it },
                settingsViewModel = settingsViewModel,
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
                    },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                )
            }
        }
    }
}