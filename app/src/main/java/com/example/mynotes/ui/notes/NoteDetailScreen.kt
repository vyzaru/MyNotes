package com.example.mynotes.ui.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.data.Note
import com.example.mynotes.ui.notes.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteViewModel,
    navController: NavController,
    noteId: Int?
) {
    val titleState = remember { mutableStateOf("") }
    val contentState = remember { mutableStateOf("") }

    // Загрузка существующей заметки
    if (noteId != null && noteId != -1) {
        LaunchedEffect(noteId) {
            viewModel.allNotes.collectLatest { notes ->
                notes.find { it.id == noteId }?.let { note ->
                    titleState.value = note.title
                    contentState.value = note.content
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1) "Новая заметка" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Кнопка удаления (только для существующих заметок)
                    if (noteId != null && noteId != -1) {
                        IconButton(
                            onClick = {
                                viewModel.delete(
                                    Note(
                                        id = noteId,
                                        title = titleState.value,
                                        content = contentState.value
                                    )
                                )
                                navController.popBackStack()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }

                    // Кнопка сохранения
                    IconButton(
                        onClick = {
                            val note = Note(
                                id = if (noteId == -1) null else noteId,
                                title = titleState.value,
                                content = contentState.value
                            )

                            if (noteId == -1 || noteId == null) {
                                viewModel.insert(note)
                            } else {
                                viewModel.update(note)
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                label = { Text("Заголовок") },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = contentState.value,
                onValueChange = { contentState.value = it },
                label = { Text("Содержание") },
                modifier = Modifier.weight(1f),
                singleLine = false
            )
        }
    }
}