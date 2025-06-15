package com.example.mynotes.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.mynotes.data.database.dao.NoteDao
import com.example.mynotes.data.models.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class NoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val noteDao: NoteDao
) {
    init {
        try {
            // Создаем файл в общедоступной директории Downloads
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val infoFile = File(downloadsDir, "mynotes_info.txt")
            
            val info = """
                App Package: ${context.packageName}
                Data Directory: ${context.dataDir}
                Database Path: ${context.getDatabasePath("notes_db")}
                External Files: ${context.getExternalFilesDir(null)}
                Downloads Dir: $downloadsDir
            """.trimIndent()
            
            infoFile.writeText(info)
            Log.d("NoteRepository", "Created info file at: ${infoFile.absolutePath}")
            Log.d("NoteRepository", "Info content: $info")
        } catch (e: Exception) {
            Log.e("NoteRepository", "Error creating info file", e)
        }
    }

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    
    fun getNoteById(noteId: Int): Flow<Note?> {
        Log.d("NoteRepository", "Getting note by id: $noteId")
        return noteDao.getNoteById(noteId)
    }
    
    suspend fun insertNote(note: Note) {
        Log.d("NoteRepository", "Inserting note: $note")
        noteDao.insert(note)
    }
    
    suspend fun updateNote(note: Note) {
        Log.d("NoteRepository", "Updating note: $note")
        noteDao.update(note)
    }
    
    suspend fun deleteNote(note: Note) = noteDao.delete(note)
}