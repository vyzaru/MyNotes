package com.example.mynotes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.mynotes.data.models.AppSettings
import com.example.mynotes.ui.navigation.AppNavHost
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import com.example.mynotes.ui.screens.settings.viewmodel.SettingsViewModel
import com.example.mynotes.ui.theme.AppTheme
import com.example.mynotes.ui.theme.MyNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MainActivity", "Uncaught exception in thread $thread", throwable)
        }

        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Starting MainActivity onCreate")
            
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val navController = rememberNavController()
            val noteViewModel: NoteViewModel = hiltViewModel()
                
            MyNotesTheme(
                darkTheme = settings.isDarkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
            
        Log.d("MainActivity", "MainActivity onCreate completed")
    }
}