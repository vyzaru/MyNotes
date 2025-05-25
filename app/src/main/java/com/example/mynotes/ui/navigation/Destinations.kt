package com.example.mynotes.ui.navigation

sealed class Screen(val route: String) {
    object NotesList : Screen("notesList")
    object NoteDetail : Screen("noteDetail/{noteId}") {
        fun createRoute(noteId: Int) = "noteDetail/$noteId"
    }
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
}