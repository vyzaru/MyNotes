package com.example.mynotes.di

import android.content.Context
import androidx.room.Room
import com.example.mynotes.data.database.AppDatabase
import com.example.mynotes.data.database.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Здесь могут быть другие провайдеры, если они понадобятся
}