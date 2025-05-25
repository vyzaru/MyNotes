package com.example.mynotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var currentMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var currentYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

    Column(modifier = modifier.padding(16.dp)) {
        // Заголовок с месяцем и годом
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    calendar.set(currentYear, currentMonth - 1, 1)
                    currentMonth = calendar.get(Calendar.MONTH)
                    currentYear = calendar.get(Calendar.YEAR)
                }
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            
            Text(
                text = "${getMonthName(currentMonth)} $currentYear",
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(
                onClick = {
                    calendar.set(currentYear, currentMonth + 1, 1)
                    currentMonth = calendar.get(Calendar.MONTH)
                    currentYear = calendar.get(Calendar.YEAR)
                }
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Дни недели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Дни месяца
        val daysInMonth = getDaysInMonth(currentMonth, currentYear)
        val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)
        val days = (1..daysInMonth).map { day ->
            calendar.set(currentYear, currentMonth, day)
            CalendarDay(
                day = day,
                date = calendar.timeInMillis,
                isSelected = calendar.timeInMillis == selectedDate
            )
        }
        val emptyDays = (1 until firstDayOfWeek).map { null }
        val allDays = emptyDays + days

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp)
        ) {
            items(allDays) { calendarDay ->
                if (calendarDay != null) {
                    DayCell(
                        day = calendarDay.day,
                        isSelected = calendarDay.isSelected,
                        onClick = {
                            selectedDate = calendarDay.date
                            onDateSelected(calendarDay.date)
                        }
                    )
                } else {
                    Box(modifier = Modifier.size(40.dp))
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
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

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