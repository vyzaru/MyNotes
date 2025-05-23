package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.mynotes.ui.theme.MyNotesTheme
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.mynotes.data.DatabaseModule
import com.example.mynotes.ui.navigation.NotesApp
import com.example.mynotes.ui.notes.viewmodel.NoteViewModel
import com.example.mynotes.ui.notes.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        ViewModelFactory(
            DatabaseModule.provideNoteRepository(
                DatabaseModule.provideDatabase(application)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesApp(viewModel)
                }
            }
        }
    }
}