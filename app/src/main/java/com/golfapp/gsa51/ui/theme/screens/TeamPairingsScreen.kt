package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.utils.HapticFeedback
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.TeamPairingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamPairingsScreen(
    gameId: Long,
    onNavigateToScoring: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToGameRules: () -> Unit
) {
    // Create the ViewModel with the game ID
    val viewModel = viewModel<TeamPairingViewModel>(
        factory = AppViewModelProvider.Factory,
        key = "teamPairings_$gameId"
    )

    // Initialize the ViewModel with the game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // State for restart confirmation dialog
    var showRestartConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Team Pairings",
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing between elements
        ) {
            // Updated explanation text
            Text(
                text = "Configure team pairings for each 6-hole segment. Pairings will start from the starting hole you set in Game Details. Each pairing must be unique across all sections.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // First 6 Holes
            SectionCard(
                title = "First 6 Holes",  // Changed from "Holes 1-6"
                status = if (viewModel.section1Completed) "Completed" else "Select Pairing",
                statusColor = if (viewModel.section1Completed) Color.Green else GSAPurple,
                isEnabled = true,
                selectedPairing = viewModel.selectedPairingSection1,
                pairingOptions = viewModel.pairingOptions,
                onPairingSelected = { viewModel.selectPairingForSection1(it) },
                onConfirmClick = { viewModel.confirmSection1() }
            )

            // Second 6 Holes
            SectionCard(
                title = "Second 6 Holes",  // Changed from "Holes 7-12"
                status = when {
                    viewModel.section2Completed -> "Completed"
                    viewModel.section1Completed -> "Select Pairing"
                    else -> "Locked"
                },
                statusColor = when {
                    viewModel.section2Completed -> Color.Green
                    viewModel.section1Completed -> GSAPurple
                    else -> Color.Gray
                },
                isEnabled = viewModel.section1Completed && !viewModel.section2Completed,
                selectedPairing = viewModel.selectedPairingSection2,
                pairingOptions = viewModel.pairingOptions.filter { it.id != viewModel.selectedPairingSection1?.id },
                onPairingSelected = { viewModel.selectPairingForSection2(it) },
                onConfirmClick = { viewModel.confirmSection2() }
            )

            // Third 6 Holes
            SectionCard(
                title = "Third 6 Holes",  // Changed from "Holes 13-18"
                status = when {
                    viewModel.section3Completed -> "Completed"
                    viewModel.section2Completed -> "Auto-filled"
                    else -> "Locked"
                },
                statusColor = when {
                    viewModel.section3Completed -> Color.Green
                    viewModel.section2Completed -> Color(0xFFFFA500) // Orange
                    else -> Color.Gray
                },
                isEnabled = false, // Always disabled as it's auto-filled
                selectedPairing = viewModel.selectedPairingSection3,
                pairingOptions = emptyList(), // No options needed as it's auto-filled
                onPairingSelected = { /* No-op */ },
                onConfirmClick = { /* No-op */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons - swapped positions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // RESTART button - now on the left
                OutlinedButton(
                    onClick = {
                        HapticFeedback.performLightClick(context)
                        // Show confirmation dialog
                        showRestartConfirmation = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GSAPurple
                    )
                ) {
                    Text(
                        text = "RESTART",  // Shortened from "RESTART SELECTION" to fit better
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // START GAME button - now on the right
                val buttonColor = if (viewModel.allSelectionsComplete) Color(0xFF4CAF50) else GSAPurple

                Button(
                    onClick = {
                        HapticFeedback.performMediumClick(context)
                        onNavigateToScoring(gameId)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = viewModel.allSelectionsComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        disabledContainerColor = GSAPurple.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "START GAME",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

    // Restart confirmation dialog
    if (showRestartConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestartConfirmation = false },
            title = { Text("Restart Selection") },
            text = { Text("Are you sure you want to restart the team selection? All current selections will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticFeedback.performLightClick(context)
                        viewModel.resetSelections()
                        showRestartConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GSAPurple
                    )
                ) {
                    Text("YES")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestartConfirmation = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GSAPurple
                    )
                ) {
                    Text("NO")
                }
            }
        )
    }

    // Show loading indicator when needed
    if (viewModel.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GSAPurple)
        }
    }
}

@Composable
fun TeamPairingOptionCard(
    pairingOption: TeamPairingViewModel.PairingOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                HapticFeedback.performLightClick(context)
                onSelect()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) GSAPurple else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Extract team info from the description
            val teams = pairingOption.description.split(" vs ")

            // Team 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Team 1:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = GSAPurple
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = teams[0],
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Team 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Team 2:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = GSAPurple
                )
                Spacer(modifier = Modifier.width(8.dp))

                if (teams.size > 1) {
                    Text(
                        text = teams[1],
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Selected indicator
            if (isSelected) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = GSAPurple
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionCard(
    title: String,
    status: String,
    statusColor: Color,
    isEnabled: Boolean,
    selectedPairing: TeamPairingViewModel.PairingOption?,
    pairingOptions: List<TeamPairingViewModel.PairingOption>,
    onPairingSelected: (TeamPairingViewModel.PairingOption) -> Unit,
    onConfirmClick: () -> Unit
) {
    val context = LocalContext.current
    val isCompleted = status == "Completed" // Add this line to check completion status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header row with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }

            // Only show options if enabled AND not completed
            if (isEnabled && !isCompleted) {
                // Text label
                Text(
                    text = "Select a team pairing for $title:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Display team pairing options as cards
                pairingOptions.forEach { option ->
                    TeamPairingOptionCard(
                        pairingOption = option,
                        isSelected = selectedPairing?.id == option.id,
                        onSelect = { onPairingSelected(option) }
                    )
                }

                // Confirm button if a pairing is selected
                if (selectedPairing != null) {
                    Button(
                        onClick = {
                            HapticFeedback.performMediumClick(context)
                            onConfirmClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(40.dp),
                        enabled = selectedPairing != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GSAPurple
                        )
                    ) {
                        Text(
                            text = "CONFIRM",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else if (selectedPairing != null) {
                // If disabled or completed but has a selected pairing, show the teams
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val description = selectedPairing.description
                    val teams = description.split(" vs ")

                    if (teams.size == 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Team 1: ${teams[0]}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Team 2: ${teams[1]}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}