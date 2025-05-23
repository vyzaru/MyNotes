package com.example.mynotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mynotes.ui.notes.NoteDetailScreen
import com.example.mynotes.ui.notes.viewmodel.NoteViewModel
import com.example.mynotes.ui.notes.NotesListScreen

@Composable
fun NotesApp(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") {
            NotesListScreen(viewModel, navController)
        }
        composable("noteDetail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            NoteDetailScreen(viewModel, navController, noteId)
        }
    }
}