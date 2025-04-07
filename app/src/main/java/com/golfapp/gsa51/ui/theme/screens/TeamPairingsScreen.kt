package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.TeamPairingViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar

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
                    onClick = { onNavigateToScoring(gameId) },
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
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                .padding(12.dp), // Reduced padding
            verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
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

            // Only show dropdown and team info if enabled
            if (isEnabled) {
                // Custom dropdown implementation with reduced vertical space
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
                ) {
                    // Custom dropdown field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = isEnabled) { expanded = !expanded },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, if (expanded) GSAPurple else Color.Gray),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp), // Reduced padding
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedPairing?.description ?: "Select pairing",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedPairing == null) Color.Gray else Color.Black
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Dropdown",
                                modifier = Modifier.rotate(90f), // Rotate to make it point down
                                tint = GSAPurple
                            )
                        }
                    }

                    // Dropdown menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .background(Color.White)
                    ) {
                        pairingOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onPairingSelected(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Show selected teams when a pairing is selected
                if (selectedPairing != null) {
                    // More compact display of team members
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp), // Reduced padding
                        verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing
                    ) {
                        val description = selectedPairing.description
                        val teams = description.split(" vs ")

                        if (teams.size == 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Team 1: ${teams[0]}",
                                    style = MaterialTheme.typography.bodySmall // Smaller text
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Team 2: ${teams[1]}",
                                    style = MaterialTheme.typography.bodySmall // Smaller text
                                )
                            }
                        }
                    }

                    // Confirm button
                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp) // Reduced padding
                            .height(40.dp), // Shorter button
                        enabled = selectedPairing != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GSAPurple
                        )
                    ) {
                        Text(
                            text = "CONFIRM",
                            style = MaterialTheme.typography.labelSmall // Smaller text
                        )
                    }
                }
            } else if (selectedPairing != null) {
                // If disabled but has a selected pairing, show the teams in a more compact format
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp), // Reduced padding
                    verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing
                ) {
                    val description = selectedPairing.description
                    val teams = description.split(" vs ")

                    if (teams.size == 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Team 1: ${teams[0]}",
                                style = MaterialTheme.typography.bodySmall // Smaller text
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Team 2: ${teams[1]}",
                                style = MaterialTheme.typography.bodySmall // Smaller text
                            )
                        }
                    }
                }
            }
        }
    }
}