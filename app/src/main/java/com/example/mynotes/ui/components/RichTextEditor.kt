package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import com.example.mynotes.R

@Composable
fun RichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    onFormattedValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    onTextColorChange: (Color) -> Unit
) {
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isBulletPoint by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = value)
        }
    }

    fun getCurrentLineStart(text: String, cursorPosition: Int): Int {
        return text.lastIndexOf('\n', cursorPosition - 1).let { if (it == -1) 0 else it + 1 }
    }

    fun getCurrentLineEnd(text: String, cursorPosition: Int): Int {
        return text.indexOf('\n', cursorPosition).let { if (it == -1) text.length else it }
    }

    fun isCurrentLineBulleted(text: String, cursorPosition: Int): Boolean {
        val lineStart = getCurrentLineStart(text, cursorPosition)
        return text.substring(lineStart).trimStart().startsWith("• ")
    }

    fun toggleBulletForCurrentLine(text: String, cursorPosition: Int): Pair<String, Int> {
        val lineStart = getCurrentLineStart(text, cursorPosition)
        val lineEnd = getCurrentLineEnd(text, cursorPosition)
        val line = text.substring(lineStart, lineEnd)
        val restOfText = text.substring(lineEnd)
        
        val trimmedLine = line.trimStart()
        val leadingSpaces = line.substring(0, line.length - trimmedLine.length)
        
        return if (trimmedLine.startsWith("• ")) {
            // Удаляем маркер
            val newText = text.substring(0, lineStart) + leadingSpaces + trimmedLine.substring(2) + restOfText
            val newPosition = cursorPosition - 2
            Pair(newText, newPosition)
        } else {
            // Добавляем маркер
            val newText = text.substring(0, lineStart) + leadingSpaces + "• " + trimmedLine + restOfText
            val newPosition = cursorPosition + 2
            Pair(newText, newPosition)
        }
    }

    fun handleEnterInBulletList(text: String, cursorPosition: Int): Pair<String, Int> {
        val lineStart = getCurrentLineStart(text, cursorPosition)
        val lineEnd = getCurrentLineEnd(text, cursorPosition)
        val currentLine = text.substring(lineStart, lineEnd)
        val trimmedLine = currentLine.trimStart()
        
        return if (trimmedLine == "• " || trimmedLine.isEmpty()) {
            // Если строка пустая или содержит только маркер, удаляем маркер
            val newText = text.substring(0, lineStart) + "\n" + text.substring(lineEnd)
            val newPosition = lineStart + 1
            isBulletPoint = false
            Pair(newText, newPosition)
        } else {
            // Добавляем новую строку с маркером
            val leadingSpaces = currentLine.substring(0, currentLine.length - trimmedLine.length)
            val newText = text.substring(0, lineEnd) + "\n" + leadingSpaces + "• "
            val newPosition = lineEnd + 3 + leadingSpaces.length
            Pair(newText, newPosition)
        }
    }

    Column(modifier = modifier) {
        // Панель инструментов форматирования
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { 
                    isBold = !isBold
                    val selection = textFieldValue.selection
                    if (!selection.collapsed) {
                        val beforeSelection = textFieldValue.text.substring(0, selection.start)
                        val selectedText = textFieldValue.text.substring(selection.start, selection.end)
                        val afterSelection = textFieldValue.text.substring(selection.end)
                        val newText = beforeSelection + (if (isBold) "<b>$selectedText</b>" else selectedText) + afterSelection
                        textFieldValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(selection.start, selection.end + (if (isBold) 7 else -7))
                        )
                        onValueChange(newText)
                        onFormattedValueChange(newText)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isBold) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatBold, stringResource(R.string.bold))
            }

            IconButton(
                onClick = { 
                    isItalic = !isItalic
                    val selection = textFieldValue.selection
                    if (!selection.collapsed) {
                        val beforeSelection = textFieldValue.text.substring(0, selection.start)
                        val selectedText = textFieldValue.text.substring(selection.start, selection.end)
                        val afterSelection = textFieldValue.text.substring(selection.end)
                        val newText = beforeSelection + (if (isItalic) "<i>$selectedText</i>" else selectedText) + afterSelection
                        textFieldValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(selection.start, selection.end + (if (isItalic) 7 else -7))
                        )
                        onValueChange(newText)
                        onFormattedValueChange(newText)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isItalic) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatItalic, stringResource(R.string.italic))
            }

            IconButton(
                onClick = { 
                    val cursorPosition = textFieldValue.selection.start
                    val (newText, newPosition) = toggleBulletForCurrentLine(textFieldValue.text, cursorPosition)
                    textFieldValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newPosition)
                    )
                    isBulletPoint = !isBulletPoint
                    onValueChange(newText)
                    onFormattedValueChange(newText)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isBulletPoint) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            ) {
                Icon(Icons.Default.FormatListBulleted, stringResource(R.string.bullet_list))
            }

            IconButton(onClick = { showColorPicker = true }) {
                Icon(Icons.Default.Palette, stringResource(R.string.text_color))
            }
        }

        // Поле для ввода текста
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val oldText = textFieldValue.text
                val newText = newValue.text
                val cursorPosition = newValue.selection.start
                
                // Проверяем, был ли нажат Enter
                if (isBulletPoint && 
                    newText.length > oldText.length && 
                    newText.endsWith("\n") &&
                    isCurrentLineBulleted(oldText, cursorPosition - 1)
                ) {
                    val (processedText, newPosition) = handleEnterInBulletList(oldText, cursorPosition - 1)
                    textFieldValue = TextFieldValue(
                        text = processedText,
                        selection = TextRange(newPosition)
                    )
                } else {
                    textFieldValue = newValue
                }
                
                onValueChange(textFieldValue.text)
                onFormattedValueChange(textFieldValue.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.note_content_hint),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            }
        )

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
                                    .padding(4.dp),
                                color = color,
                                onClick = {
                                    val selection = textFieldValue.selection
                                    if (!selection.collapsed) {
                                        val beforeSelection = textFieldValue.text.substring(0, selection.start)
                                        val selectedText = textFieldValue.text.substring(selection.start, selection.end)
                                        val afterSelection = textFieldValue.text.substring(selection.end)
                                        val colorHex = String.format("#%06X", 0xFFFFFF and color.toArgb())
                                        val newText = beforeSelection + 
                                            "<font color='$colorHex'>$selectedText</font>" + 
                                            afterSelection
                                        textFieldValue = TextFieldValue(
                                            text = newText,
                                            selection = TextRange(selection.start, selection.end + 23 + colorHex.length)
                                        )
                                        onValueChange(newText)
                                        onFormattedValueChange(newText)
                                    } else {
                                        onTextColorChange(color)
                                    }
                                    showColorPicker = false
                                }
                            ) {}
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
} 