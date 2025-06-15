package com.example.mynotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mynotes.ui.screens.calendar.CalendarScreen
import com.example.mynotes.ui.screens.notes.NoteDetailScreen
import com.example.mynotes.ui.screens.notes.NotesListScreen
import com.example.mynotes.ui.screens.notes.viewmodel.NoteViewModel
import com.example.mynotes.ui.screens.settings.SettingsScreen
import com.example.mynotes.ui.screens.settings.viewmodel.SettingsViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route
    ) {
        composable(Screen.Settings.route) {
            SettingsScreen(navController, settingsViewModel)
        }
        composable(Screen.NotesList.route) {
            NotesListScreen(navController, noteViewModel)
        }
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("date") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date")
            val date = dateString?.toLongOrNull()
            NoteDetailScreen(
                navController = navController,
                viewModel = noteViewModel,
                settingsViewModel = settingsViewModel,
                noteId = backStackEntry.arguments?.getInt("noteId") ?: -1,
                initialDate = date
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(navController, noteViewModel)
        }
    }
}