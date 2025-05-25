package com.example.mynotes.data.database

import android.app.Application
import androidx.room.Room
import com.example.mynotes.data.repository.NoteRepository

object DatabaseModule {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "notes_db"
        ).build()
    }

    fun provideNoteRepository(database: AppDatabase): NoteRepository {
        return NoteRepository(database.noteDao())
    }
}