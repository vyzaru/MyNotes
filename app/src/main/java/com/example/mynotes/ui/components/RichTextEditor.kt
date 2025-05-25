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
    var textFieldValue by remember(value) { mutableStateOf(TextFieldValue(value)) }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = TextFieldValue(value)
        }
    }

    fun handleBulletList(text: String, cursorPosition: Int): Pair<String, Int> {
        val lines = text.split("\n")
        var currentPos = 0
        var newCursorPosition = cursorPosition
        val newLines = mutableListOf<String>()
        
        for (line in lines) {
            if (cursorPosition > currentPos && cursorPosition <= currentPos + line.length + 1) {
                // Курсор находится в этой строке
                if (line.startsWith("• ")) {
                    // Если это уже пункт списка и нажат Enter
                    if (line.substring(2).trim().isEmpty()) {
                        // Если строка пустая (кроме маркера), удаляем маркер
                        newLines.add("")
                        newCursorPosition = currentPos
                    } else {
                        // Добавляем новый пункт списка
                        newLines.add(line)
                        newLines.add("• ")
                        newCursorPosition = currentPos + line.length + 3
                    }
                } else if (isBulletPoint) {
                    // Добавляем маркер к текущей строке
                    newLines.add("• $line")
                    newCursorPosition += 2
                } else {
                    newLines.add(line)
                }
            } else {
                newLines.add(line)
            }
            currentPos += line.length + 1
        }
        
        return Pair(newLines.joinToString("\n"), newCursorPosition)
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
                    isBulletPoint = !isBulletPoint
                    val (newText, newPosition) = handleBulletList(
                        textFieldValue.text,
                        textFieldValue.selection.start
                    )
                    textFieldValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newPosition)
                    )
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
                
                textFieldValue = if (newText.length > oldText.length && 
                    newText.endsWith("\n") && 
                    oldText.length > 0 && 
                    oldText[oldText.length - 1] != '\n') {
                    // Обработка нажатия Enter
                    val (processedText, newPosition) = handleBulletList(
                        newText,
                        newValue.selection.start
                    )
                    TextFieldValue(
                        text = processedText,
                        selection = TextRange(newPosition)
                    )
                } else {
                    newValue
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