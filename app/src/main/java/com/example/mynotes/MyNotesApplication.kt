package com.example.mynotes

import android.app.Application
import android.util.Log
import com.example.mynotes.data.database.AppDatabase
import com.example.mynotes.data.models.AppSettings
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyNotesApplication : Application() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        Log.d("MyNotesApplication", "Application onCreate called")
        
        // Инициализация настроек по умолчанию
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settingsDao = database.settingsDao()
                val settings = settingsDao.getSettings().collect { currentSettings ->
                    if (currentSettings == null) {
                        Log.d("MyNotesApplication", "Initializing default settings")
                        settingsDao.insert(AppSettings())
                    }
                }
            } catch (e: Exception) {
                Log.e("MyNotesApplication", "Error initializing settings", e)
            }
        }
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MyNotesApplication", "Uncaught exception in thread $thread", throwable)
        }
    }
} 