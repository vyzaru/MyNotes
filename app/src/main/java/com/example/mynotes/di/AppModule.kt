package com.example.mynotes.di

import android.content.Context
import androidx.room.Room
import com.example.mynotes.data.database.AppDatabase
import com.example.mynotes.data.database.MIGRATION_1_2
import com.example.mynotes.data.database.dao.NoteDao
import com.example.mynotes.data.database.dao.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_db"
        ).addMigrations(*AppDatabase.migrations)
            .build()
    }

    // Добавьте эти методы:
    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }
}