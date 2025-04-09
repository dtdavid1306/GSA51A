package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.ui.theme.components.GSAPrimaryButton
import com.golfapp.gsa51.viewmodels.AdvancedSettingsViewModel
import com.golfapp.gsa51.viewmodels.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGameRules: () -> Unit,
    viewModel: AdvancedSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Advanced Settings",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onInfoClick = onNavigateToGameRules
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Maximum Score Limit",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Set the maximum score allowed for each hole (default: 10)",
                style = MaterialTheme.typography.bodyMedium
            )

            // Slider for max score selection (1-20 range)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.maxScoreLimit.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )

                Slider(
                    value = viewModel.maxScoreLimit.toFloat(),
                    onValueChange = { viewModel.updateMaxScoreLimit(it.toInt()) },
                    valueRange = 1f..20f,
                    steps = 18,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Min: 1", style = MaterialTheme.typography.bodySmall)
                    Text("Max: 20", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Show success message if present
            viewModel.saveSuccessMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Save button
            GSAPrimaryButton(
                text = "SAVE SETTINGS",
                onClick = {
                    viewModel.saveSettings {
                        // Wait a moment to show success message before navigating back
                        // This would normally be handled by the ViewModel's saveSettings method
                    }
                }
            )

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GSAPurple)
                }
            }
        }
    }
}