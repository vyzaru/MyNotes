package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R
import com.example.mynotes.data.models.AppSettings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mynotes.ui.screens.settings.viewmodel.SettingsViewModel

data class TextStyle(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isBulletList: Boolean = false
)

data class FormattedText(
    val text: String,
    val styles: List<TextStyleRange>,
    val bulletLines: Set<Int> = emptySet()
)

data class TextStyleRange(
    val style: TextStyle,
    val start: Int,
    val end: Int
)

@Composable
fun RichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    onFormattedValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    onTextColorChange: (Color) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var currentStyle by remember { mutableStateOf(TextStyle()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var formattedText by remember { mutableStateOf(FormattedText(value, emptyList())) }
    var isInBulletList by remember { mutableStateOf(false) }
    var bulletListStartLine by remember { mutableStateOf(-1) }
    var bulletListEndLine by remember { mutableStateOf(-1) }

    // Инициализация при загрузке текста
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = textFieldValue.selection
            )
            formattedText = FormattedText(value, emptyList())
            
            // Проверяем, находимся ли мы в маркированном списке
            val lines = value.lines()
            var startLine = -1
            var endLine = -1
            for (i in lines.indices) {
                if (lines[i].startsWith("• ")) {
                    if (startLine == -1) startLine = i
                    endLine = i
                } else if (startLine != -1 && endLine != -1) {
                    break
                }
            }
            isInBulletList = startLine != -1
            bulletListStartLine = startLine
            bulletListEndLine = endLine
            currentStyle = currentStyle.copy(isBulletList = isInBulletList)
        }
    }

    // Функция для обработки ввода текста
    fun handleTextInput(newValue: TextFieldValue) {
        val selection = newValue.selection
        val newText = newValue.text
        val oldText = textFieldValue.text
        
        // Проверяем, является ли это удалением символа
        val isDeletion = newText.length < oldText.length
        
        // Находим текущую строку и позицию курсора
        val cursorLine = newText.substring(0, selection.start).count { it == '\n' }
        val cursorOffset = selection.start - newText.split("\n").take(cursorLine).sumOf { it.length + 1 }
        
        // Разбиваем текст на строки
        val lines = newText.split("\n")
        val oldLines = oldText.split("\n")
        
        // Определяем, является ли это переносом строки
        val isNewLine = newText.length > oldText.length && newText[selection.start - 1] == '\n'
        
        // Проверяем, нужно ли отключить маркированный список
        if (isDeletion && cursorOffset == 0) {
            val currentLine = oldLines.getOrNull(cursorLine) ?: ""
            if (currentLine.startsWith("• ")) {
                // Если удаляем маркер, отключаем список
                currentStyle = currentStyle.copy(isBulletList = false)
            }
        }
        
        // Обрабатываем каждую строку
        val processedLines = lines.mapIndexed { index, line ->
            when {
                // Если это перенос строки
                isNewLine && index == cursorLine -> {
                    val prevLine = oldLines.getOrNull(index - 1) ?: ""
                    // Добавляем маркер только если предыдущая строка была маркированной и мы в списке
                    if (prevLine.startsWith("• ") && currentStyle.isBulletList && line.isEmpty()) {
                        "• "
                    } else {
                        line
                    }
                }
                // Если это существующая маркированная строка
                line.startsWith("• ") -> {
                    // Если пытаемся удалить маркер (курсор в начале строки и нажали backspace)
                    if (isDeletion && cursorOffset == 0 && index == cursorLine) {
                        // Удаляем маркер
                        line.substring(2)
                    } else {
                        line
                    }
                }
                // Если это пустая строка после маркированной
                index > 0 && line.isEmpty() && oldLines.getOrNull(index - 1)?.startsWith("• ") == true -> {
                    if (cursorOffset == 0 && currentStyle.isBulletList) {
                        "• "
                    } else {
                        line
                    }
                }
                // Если это новая строка после маркированной (не через перенос)
                index > 0 && oldLines.getOrNull(index - 1)?.startsWith("• ") == true && !line.startsWith("• ") -> {
                    if (cursorOffset == 0 && currentStyle.isBulletList) {
                        "• $line"
                    } else {
                        line
                    }
                }
                // Все остальные случаи
                else -> line
            }
        }
        
        val processedText = processedLines.joinToString("\n")
        
        // Корректируем позицию курсора
        val newSelection = if (newText != processedText) {
            val currentLineStart = processedText.split("\n").take(cursorLine).sumOf { it.length + 1 }
            val currentLine = processedLines.getOrNull(cursorLine) ?: ""
            
            when {
                // Если мы удалили маркер
                isDeletion && cursorOffset == 0 && currentLine.startsWith("• ") -> {
                    TextRange(currentLineStart)
                }
                // Если мы добавили маркер
                !isDeletion && cursorOffset == 0 && currentLine.startsWith("• ") -> {
                    TextRange(currentLineStart + 2)
                }
                // Если это перенос строки
                isNewLine && cursorLine == cursorLine -> {
                    TextRange(currentLineStart + cursorOffset)
                }
                // Все остальные случаи
                else -> {
                    TextRange(currentLineStart + cursorOffset)
                }
            }
        } else {
            selection
        }
        
        textFieldValue = TextFieldValue(
            text = processedText,
            selection = newSelection
        )
        formattedText = FormattedText(processedText, formattedText.styles)
        
        onValueChange(processedText)
        onFormattedValueChange(processedText)
    }

    // Функция для переключения форматирования
    fun toggleFormatting(isBold: Boolean) {
        val text = textFieldValue.text
        val selection = textFieldValue.selection
        val cursorPosition = selection.start
        
        // Находим текущую строку
        val beforeCursor = text.substring(0, cursorPosition)
        val lastNewLine = beforeCursor.lastIndexOf("\n")
        val lineStart = if (lastNewLine == -1) 0 else lastNewLine + 1
        val currentLine = beforeCursor.count { it == '\n' }
        
        // Получаем текущую строку
        val currentLineText = text.substring(lineStart).split("\n").first()
        
        // Создаем новый стиль
        val newStyle = if (isBold) {
            currentStyle.copy(isBold = !currentStyle.isBold)
        } else {
            currentStyle.copy(isItalic = !currentStyle.isItalic)
        }
        
        // Обновляем стили
        val newStyles = formattedText.styles.toMutableList()
        newStyles.add(TextStyleRange(newStyle, lineStart, lineStart + currentLineText.length))
        
        formattedText = FormattedText(text, newStyles)
        currentStyle = newStyle
        
        onValueChange(text)
        onFormattedValueChange(text)
    }

    // Функция для переключения маркированного списка
    fun toggleBulletList() {
        val text = textFieldValue.text
        val selection = textFieldValue.selection
        val cursorPosition = selection.start
        
        // Находим текущую строку
        val beforeCursor = text.substring(0, cursorPosition)
        val lastNewLine = beforeCursor.lastIndexOf("\n")
        val lineStart = if (lastNewLine == -1) 0 else lastNewLine + 1
        
        // Получаем текущую строку
        val currentLineText = text.substring(lineStart).split("\n").first()
        val hasBullet = currentLineText.startsWith("• ")
        
        // Создаем новую версию текста
        val newText = if (hasBullet) {
            // Удаляем маркер
            text.substring(0, lineStart) + currentLineText.substring(2) + text.substring(lineStart + currentLineText.length)
        } else {
            // Добавляем маркер
            text.substring(0, lineStart) + "• " + text.substring(lineStart)
        }
        
        try {
            // Обновляем состояние
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(if (hasBullet) cursorPosition - 2 else cursorPosition + 2)
            )
            
            // Обновляем форматированный текст, сохраняя только стили, не связанные с маркированным списком
            val newStyles = formattedText.styles.filter { style ->
                style.start < lineStart || style.end > lineStart + currentLineText.length
            }.toMutableList()
            
            formattedText = FormattedText(newText, newStyles)
            currentStyle = currentStyle.copy(isBulletList = !hasBullet)
            
            onValueChange(newText)
            onFormattedValueChange(newText)
        } catch (e: Exception) {
            // В случае ошибки просто обновляем текст без стилей
            textFieldValue = TextFieldValue(newText)
            formattedText = FormattedText(newText, emptyList())
            currentStyle = currentStyle.copy(isBulletList = !hasBullet)
            onValueChange(newText)
            onFormattedValueChange(newText)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { toggleFormatting(true) }
            ) {
                Icon(
                    imageVector = Icons.Default.FormatBold,
                    contentDescription = stringResource(R.string.bold),
                    tint = if (currentStyle.isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { toggleFormatting(false) }
            ) {
                Icon(
                    imageVector = Icons.Default.FormatItalic,
                    contentDescription = stringResource(R.string.italic),
                    tint = if (currentStyle.isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { toggleBulletList() }
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListBulleted,
                    contentDescription = stringResource(R.string.bullet_list),
                    tint = if (currentStyle.isBulletList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { showColorPicker = true }
            ) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = stringResource(R.string.text_color),
                    tint = textColor
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            val annotatedText = buildAnnotatedString {
                append(formattedText.text)
                formattedText.styles.forEach { styleRange ->
                    addStyle(
                        SpanStyle(
                            fontWeight = if (styleRange.style.isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (styleRange.style.isItalic) FontStyle.Italic else FontStyle.Normal
                        ),
                        styleRange.start,
                        styleRange.end
                    )
                }
            }

            BasicTextField(
                value = textFieldValue,
                onValueChange = { handleTextInput(it) },
                textStyle = TextStyle(
                    color = Color.Transparent,
                    fontSize = settings.fontSize.sp,
                    fontFamily = when (settings.selectedFontFamily) {
                        "Cursive" -> FontFamily.Cursive
                        else -> FontFamily.Default
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        Text(
                            text = annotatedText,
                            style = TextStyle(
                                color = textColor,
                                fontSize = settings.fontSize.sp,
                                fontFamily = when (settings.selectedFontFamily) {
                                    "Cursive" -> FontFamily.Cursive
                                    else -> FontFamily.Default
                                }
                            )
                        )
                        innerTextField()
                    }
                }
            )
        }
    }

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text(stringResource(R.string.choose_color)) },
            text = {
                Column {
                    listOf(
                        Color.Black,
                        Color.Red,
                        Color.Blue,
                        Color.Green,
                        Color.Gray
                    ).forEach { color ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(4.dp)
                                .clickable {
                                    onTextColorChange(color)
                                    showColorPicker = false
                                },
                            color = color
                        ) {}
                    }
                }
            },
            confirmButton = {}
        )
    }
} 