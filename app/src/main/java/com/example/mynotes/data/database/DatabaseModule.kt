package com.example.mynotes.data.database

import android.content.Context
import androidx.room.Room
import com.example.mynotes.data.database.dao.NoteDao
import com.example.mynotes.data.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_db"
        )
        .addMigrations(*AppDatabase.migrations)
        .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        @ApplicationContext context: Context,
        noteDao: NoteDao
    ): NoteRepository {
        return NoteRepository(context, noteDao)
    }
}