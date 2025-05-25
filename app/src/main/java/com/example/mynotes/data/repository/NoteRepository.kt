package com.example.mynotes.data.repository

import com.example.mynotes.data.database.dao.NoteDao
import com.example.mynotes.data.models.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    
    fun getNoteById(noteId: Int): Flow<Note?> {
        android.util.Log.d("NoteRepository", "Getting note by id: $noteId")
        return noteDao.getNoteById(noteId)
    }
    
    suspend fun insertNote(note: Note) {
        android.util.Log.d("NoteRepository", "Inserting note: $note")
        noteDao.insert(note)
    }
    
    suspend fun updateNote(note: Note) {
        android.util.Log.d("NoteRepository", "Updating note: $note")
        noteDao.update(note)
    }
    
    suspend fun deleteNote(note: Note) = noteDao.delete(note)
}