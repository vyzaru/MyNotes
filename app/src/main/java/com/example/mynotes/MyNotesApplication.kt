package com.example.mynotes

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MyNotesApplication", "Application onCreate called")
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MyNotesApplication", "Uncaught exception in thread $thread", throwable)
        }
    }
} 