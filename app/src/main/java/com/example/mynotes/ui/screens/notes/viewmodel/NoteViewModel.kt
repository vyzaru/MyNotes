package com.example.mynotes.ui.screens.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.models.Note
import com.example.mynotes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    val allNotes: Flow<List<Note>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getNoteById(noteId: Int): Flow<Note?> = repository.getNoteById(noteId)
        .onEach { note ->
            android.util.Log.d("NoteViewModel", "Loading note: $note")
        }

    fun insert(note: Note) = viewModelScope.launch {
        val noteToInsert = note.copy(
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
        android.util.Log.d("NoteViewModel", "Inserting note: $noteToInsert")
        repository.insertNote(noteToInsert)
    }

    fun update(note: Note) = viewModelScope.launch {
        val noteToUpdate = note.copy(
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
        android.util.Log.d("NoteViewModel", "Updating note: $noteToUpdate")
        repository.updateNote(noteToUpdate)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }
}