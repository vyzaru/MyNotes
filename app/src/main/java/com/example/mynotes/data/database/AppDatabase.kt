package com.example.mynotes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mynotes.data.database.dao.NoteDao
import com.example.mynotes.data.database.dao.SettingsDao
import com.example.mynotes.data.models.AppSettings
import com.example.mynotes.data.models.Note

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Миграция для таблицы notes
        // Получаем информацию о существующих колонках
        val cursor = database.query("SELECT * FROM notes LIMIT 1")
        val columnNames = cursor.columnNames.toList()
        cursor.close()

        // Создаем временную таблицу с новой схемой
        database.execSQL("""
            CREATE TABLE notes_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                color INTEGER NOT NULL DEFAULT 0xFFFFFFFF,
                createdAt INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Формируем SQL для копирования данных с учетом существующих колонок
        val columns = mutableListOf<String>()
        val values = mutableListOf<String>()
        
        // id всегда присутствует как PRIMARY KEY
        columns.add("id")
        values.add("COALESCE(id, 0)")
        
        // Добавляем остальные колонки, если они существуют
        if (columnNames.contains("title")) {
            columns.add("title")
            values.add("title")
        } else {
            columns.add("title")
            values.add("''")
        }
        
        if (columnNames.contains("content")) {
            columns.add("content")
            values.add("content")
        } else {
            columns.add("content")
            values.add("''")
        }
        
        if (columnNames.contains("color")) {
            columns.add("color")
            values.add("color")
        } else {
            columns.add("color")
            values.add("0xFFFFFFFF")
        }
        
        if (columnNames.contains("createdAt")) {
            columns.add("createdAt")
            values.add("createdAt")
        } else {
            columns.add("createdAt")
            values.add("0")
        }
        
        if (columnNames.contains("updatedAt")) {
            columns.add("updatedAt")
            values.add("updatedAt")
        } else {
            columns.add("updatedAt")
            values.add("0")
        }
        
        // Копируем данные
        val insertSql = """
            INSERT INTO notes_temp (${columns.joinToString(", ")})
            SELECT ${values.joinToString(", ")}
            FROM notes
        """
        database.execSQL(insertSql)
        
        // Удаляем старую таблицу
        database.execSQL("DROP TABLE notes")
        
        // Переименовываем временную таблицу
        database.execSQL("ALTER TABLE notes_temp RENAME TO notes")

        // Создаем таблицу app_settings
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS app_settings (
                id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                isDarkTheme INTEGER NOT NULL DEFAULT 0,
                selectedFontFamily TEXT NOT NULL DEFAULT 'sans-serif',
                fontSize REAL NOT NULL DEFAULT 16.0
            )
        """)

        // Вставляем значения по умолчанию, если таблица пуста
        database.execSQL("""
            INSERT OR IGNORE INTO app_settings (id, isDarkTheme, selectedFontFamily, fontSize)
            VALUES (0, 0, 'sans-serif', 16.0)
        """)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Создаем временную таблицу с новой схемой
        database.execSQL("""
            CREATE TABLE notes_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                formattedContent TEXT NOT NULL DEFAULT '',
                textColor INTEGER NOT NULL DEFAULT 0xFF000000,
                backgroundColor INTEGER NOT NULL DEFAULT 0xFFFFFFFF,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                scheduledDate INTEGER
            )
        """)
        
        // Копируем данные, используя color вместо backgroundColor
        database.execSQL("""
            INSERT INTO notes_temp (id, title, content, backgroundColor, createdAt, updatedAt)
            SELECT id, title, content, color, createdAt, updatedAt
            FROM notes
        """)
        
        // Удаляем старую таблицу
        database.execSQL("DROP TABLE notes")
        
        // Переименовываем временную таблицу
        database.execSQL("ALTER TABLE notes_temp RENAME TO notes")
    }
}

@Database(
    entities = [Note::class, AppSettings::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun settingsDao(): SettingsDao
    
    companion object {
        val migrations = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
    }
}