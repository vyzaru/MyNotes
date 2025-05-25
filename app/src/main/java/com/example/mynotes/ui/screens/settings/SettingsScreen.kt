package com.example.mynotes.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.models.AppSettings
import com.example.mynotes.ui.screens.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settings.collectAsState(initial = AppSettings())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.dark_theme), modifier = Modifier.weight(1f))
                Switch(
                    checked = settings.isDarkTheme,
                    onCheckedChange = { viewModel.toggleDarkTheme(it) }
                )
            }

            Text(stringResource(R.string.font), style = MaterialTheme.typography.titleMedium)
            Row {
                listOf("Roboto", "Cursive").forEach { font ->
                    FilterChip(
                        selected = settings.selectedFontFamily == font,
                        onClick = { viewModel.updateFont(font) },
                        label = { Text(font) }
                    )
                }
            }

            Text(stringResource(R.string.font_size, settings.fontSize.toInt()))
            Slider(
                value = settings.fontSize,
                onValueChange = { viewModel.updateFontSize(it) },
                valueRange = 12f..24f,
                steps = 12
            )
        }
    }
}