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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import com.example.mynotes.R

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
    onTextColorChange: (Color) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var currentStyle by remember { mutableStateOf(TextStyle()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var formattedText by remember { mutableStateOf(FormattedText(value, emptyList())) }

    // Обновляем значение при изменении входного параметра
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            val currentSelection = textFieldValue.selection
            textFieldValue = TextFieldValue(
                text = value,
                selection = currentSelection
            )
            formattedText = FormattedText(value, emptyList())
        }
    }

    // Функция для обработки ввода текста
    fun handleTextInput(newValue: TextFieldValue) {
        val selection = newValue.selection
        val newText = newValue.text
        
        // Обработка маркированного списка
        if (currentStyle.isBulletList) {
            // Проверяем, был ли добавлен перенос строки
            val oldText = textFieldValue.text
            val oldLines = oldText.split("\n")
            val newLines = newText.split("\n")
            
            if (newLines.size > oldLines.size) {
                // Добавляем маркер к новой строке
                val processedLines = newLines.mapIndexed { index, line ->
                    if (index == newLines.size - 1 && line.isEmpty()) {
                        "• "
                    } else if (!line.startsWith("• ")) {
                        "• $line"
                    } else {
                        line
                    }
                }
                val processedText = processedLines.joinToString("\n")
                
                // Обновляем множество строк с маркерами
                val newBulletLines = processedLines.mapIndexed { index, line ->
                    if (line.startsWith("• ")) index else -1
                }.filter { it != -1 }.toSet()
                
                textFieldValue = TextFieldValue(
                    text = processedText,
                    selection = TextRange(processedText.length)
                )
                formattedText = formattedText.copy(
                    text = processedText,
                    bulletLines = newBulletLines
                )
            } else {
                // Обрабатываем обычный ввод текста
                val lines = newText.split("\n")
                val processedLines = lines.mapIndexed { index, line ->
                    if (formattedText.bulletLines.contains(index) || line.startsWith("• ")) {
                        if (!line.startsWith("• ")) "• $line" else line
                    } else {
                        line
                    }
                }
                val processedText = processedLines.joinToString("\n")
                
                // Корректируем позицию курсора
                val newSelection = if (newText != processedText) {
                    val cursorLine = processedText.substring(0, selection.start).count { it == '\n' }
                    val currentLineStart = processedText.split("\n").take(cursorLine).sumOf { it.length + 1 }
                    val cursorOffset = selection.start - currentLineStart
                    TextRange(currentLineStart + cursorOffset + (if (cursorOffset == 0) 2 else 0))
                } else {
                    selection
                }

                textFieldValue = TextFieldValue(
                    text = processedText,
                    selection = newSelection
                )
                formattedText = formattedText.copy(text = processedText)
            }
        } else {
            // Если маркированный список отключен, сохраняем маркеры для отмеченных строк
            val lines = newText.split("\n")
            val processedLines = lines.mapIndexed { index, line ->
                if (formattedText.bulletLines.contains(index)) {
                    if (!line.startsWith("• ")) "• $line" else line
                } else {
                    if (line.startsWith("• ")) line.substring(2) else line
                }
            }
            val processedText = processedLines.joinToString("\n")
            
            textFieldValue = TextFieldValue(
                text = processedText,
                selection = selection
            )
            formattedText = formattedText.copy(text = processedText)
        }
        
        onValueChange(textFieldValue.text)
        onFormattedValueChange(textFieldValue.text)
    }

    // Функция для переключения форматирования
    fun toggleFormatting(isBold: Boolean) {
        val selection = textFieldValue.selection
        if (!selection.collapsed) {
            val newStyle = if (isBold) {
                currentStyle.copy(isBold = !currentStyle.isBold)
            } else {
                currentStyle.copy(isItalic = !currentStyle.isItalic)
            }
            
            val newStyles = formattedText.styles.toMutableList()
            newStyles.add(TextStyleRange(newStyle, selection.start, selection.end))
            
            formattedText = formattedText.copy(styles = newStyles)
            currentStyle = newStyle
        }
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
        val currentLine = beforeCursor.count { it == '\n' }
        
        // Проверяем, есть ли уже маркер в текущей строке
        val currentLineText = text.substring(lineStart).split("\n").first()
        val hasBullet = currentLineText.startsWith("• ")
        
        if (hasBullet) {
            // Если маркер есть, удаляем его
            val newText = text.substring(0, lineStart) + currentLineText.substring(2) + text.substring(lineStart + currentLineText.length)
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(cursorPosition - 2)
            )
            formattedText = formattedText.copy(
                text = newText,
                bulletLines = formattedText.bulletLines - currentLine
            )
            currentStyle = currentStyle.copy(isBulletList = false)
        } else {
            // Если маркера нет, добавляем его
            val newText = text.substring(0, lineStart) + "• " + text.substring(lineStart)
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(cursorPosition + 2)
            )
            formattedText = formattedText.copy(
                text = newText,
                bulletLines = formattedText.bulletLines + currentLine
            )
            currentStyle = currentStyle.copy(isBulletList = true)
        }
        
        onValueChange(textFieldValue.text)
        onFormattedValueChange(textFieldValue.text)
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
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        Text(
                            text = annotatedText,
                            style = TextStyle(
                                color = textColor,
                                fontSize = 16.sp
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