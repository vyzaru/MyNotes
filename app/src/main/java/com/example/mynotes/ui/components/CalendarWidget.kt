package com.example.mynotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import java.util.*

@Composable
fun CalendarWidget(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.timeInMillis = selectedDate

    val currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    Column(modifier = modifier) {
        // Заголовок с месяцем и годом
        Text(
            text = "${getMonthName(currentMonth)} $currentYear",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        // Дни недели
        Row {
            listOf(
                R.string.monday_short,
                R.string.tuesday_short,
                R.string.wednesday_short,
                R.string.thursday_short,
                R.string.friday_short,
                R.string.saturday_short,
                R.string.sunday_short
            ).forEach { dayRes ->
                Text(
                    text = stringResource(dayRes),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Дни месяца
        val daysInMonth = getDaysInMonth(currentMonth, currentYear)
        val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)
        val days = (1..daysInMonth).map { day ->
            CalendarDay(
                day = day,
                date = createDate(day, currentMonth, currentYear),
                isSelected = false
            )
        }
        val emptyDays = (1 until firstDayOfWeek).map { null }
        val allDays = emptyDays + days

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(200.dp)
        ) {
            items(allDays) { calendarDay ->
                if (calendarDay != null) {
                    DayCell(
                        day = calendarDay.day,
                        isSelected = isSameDay(calendarDay.date, selectedDate),
                        onClick = { onDateSelected(calendarDay.date) }
                    )
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

private data class CalendarDay(
    val day: Int,
    val date: Long,
    val isSelected: Boolean
)

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.toString())
    }
}

// Вспомогательные функции
@Composable
private fun getMonthName(month: Int): String {
    return stringResource(
        when (month) {
            Calendar.JANUARY -> R.string.january
            Calendar.FEBRUARY -> R.string.february
            Calendar.MARCH -> R.string.march
            Calendar.APRIL -> R.string.april
            Calendar.MAY -> R.string.may
            Calendar.JUNE -> R.string.june
            Calendar.JULY -> R.string.july
            Calendar.AUGUST -> R.string.august
            Calendar.SEPTEMBER -> R.string.september
            Calendar.OCTOBER -> R.string.october
            Calendar.NOVEMBER -> R.string.november
            Calendar.DECEMBER -> R.string.december
            else -> R.string.january
        }
    )
}

private fun getDaysInMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

private fun getFirstDayOfWeek(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.get(Calendar.DAY_OF_WEEK)
}

private fun createDate(day: Int, month: Int, year: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.timeInMillis
}

private fun isSameDay(date1: Long, date2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}