package com.example.mynotes.data

import android.app.Application
import androidx.room.Room

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