package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.ui.theme.components.GSAPrimaryButton
import com.golfapp.gsa51.utils.HapticFeedback
import com.golfapp.gsa51.viewmodels.AdvancedSettingsViewModel
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.golfapp.gsa51.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGameRules: () -> Unit,
    viewModel: AdvancedSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Game Settings",
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
            // Maximum Score Limit Section
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

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Currency Symbol Section
            Text(
                text = "Currency Symbol",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Select the currency symbol to use for bet units",
                style = MaterialTheme.typography.bodyMedium
            )

            // Currency Options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Currency selection row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Standard currency symbols
                    listOf("$", "€", "£", "¥", "₹").forEach { symbol ->
                        CurrencyOption(
                            symbol = symbol,
                            isSelected = viewModel.currencySymbol == symbol,
                            onSelect = {
                                HapticFeedback.performLightClick(context)
                                viewModel.updateCurrencySymbol(symbol)
                            }
                        )
                    }

                    // GSA Logo option (placeholder for now)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (viewModel.currencySymbol == "GSA_LOGO") 2.dp else 1.dp,
                                color = if (viewModel.currencySymbol == "GSA_LOGO") GSAPurple else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                HapticFeedback.performLightClick(context)
                                viewModel.updateCurrencySymbol("GSA_LOGO")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder for GSA logo
                        Text(
                            text = "GSA",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (viewModel.currencySymbol == "GSA_LOGO") GSAPurple else Color.Gray
                        )
                        /* Uncomment this when you have the actual icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gsa_logo),
                            contentDescription = "GSA Logo",
                            tint = if (viewModel.currencySymbol == "GSA_LOGO") GSAPurple else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        */
                    }
                }

                // Show selected currency
                Text(
                    text = "Selected: ${if (viewModel.currencySymbol == "GSA_LOGO") "GSA Logo" else viewModel.currencySymbol}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
                    HapticFeedback.performMediumClick(context)
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

@Composable
fun CurrencyOption(
    symbol: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GSAPurple else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.headlineSmall,
            color = if (isSelected) GSAPurple else Color.Gray
        )
    }
}