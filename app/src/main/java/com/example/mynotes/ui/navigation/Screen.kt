package com.example.mynotes.ui.navigation

sealed class Screen(val route: String) {
    object NotesList : Screen("notesList")
    object NoteDetail : Screen("noteDetail/{noteId}?date={date}") {
        fun createRoute(noteId: Int, date: Long? = null): String {
            return buildString {
                append("noteDetail/$noteId")
                if (date != null) {
                    append("?date=${date.toString()}")
                }
            }
        }
    }
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
} 