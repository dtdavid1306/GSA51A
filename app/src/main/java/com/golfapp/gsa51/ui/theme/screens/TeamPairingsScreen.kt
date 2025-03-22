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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamPairingsScreen(
    gameId: Long,
    onNavigateToScoring: (Long) -> Unit,
    onNavigateBack: () -> Unit
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
            CenterAlignedTopAppBar(
                title = { Text("GSA21", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GSAPurple
                )
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
                text = "Team Pairings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Configure team pairings for each 6-hole segment. Each player can only be on one team per section. Each pairing must be unique across all sections.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Section 1: Holes 1-6
            SectionCard(
                title = "Holes 1-6",
                status = if (viewModel.section1Completed) "Completed" else "Select Pairing",
                statusColor = if (viewModel.section1Completed) Color.Green else GSAPurple,
                isEnabled = true,
                selectedPairing = viewModel.selectedPairingSection1,
                pairingOptions = viewModel.pairingOptions,
                onPairingSelected = { viewModel.selectPairingForSection1(it) },
                onConfirmClick = { viewModel.confirmSection1() }
            )

            // Section 2: Holes 7-12
            SectionCard(
                title = "Holes 7-12",
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

            // Section 3: Holes 13-18
            SectionCard(
                title = "Holes 13-18",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Button(
                onClick = { onNavigateToScoring(gameId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.allSelectionsComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GSAPurple,
                    disabledContainerColor = GSAPurple.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "START GAME",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    // Show confirmation dialog
                    showRestartConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GSAPurple
                )
            ) {
                Text(
                    text = "RESTART SELECTION",
                    style = MaterialTheme.typography.labelLarge
                )
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
            .padding(vertical = 8.dp),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

            Spacer(modifier = Modifier.height(4.dp))

            // Only show dropdown and team info if enabled
            if (isEnabled) {
                // Custom dropdown implementation
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Text label
                    Text(
                        text = "Select a team pairing for $title:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

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
                                .padding(16.dp),
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        val description = selectedPairing.description
                        val teams = description.split(" vs ")

                        if (teams.size == 2) {
                            Text(
                                text = "Team 1: ${teams[0]}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Team 2: ${teams[1]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Confirm button
                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = selectedPairing != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GSAPurple
                        )
                    ) {
                        Text(
                            text = "CONFIRM TEAMS FOR $title",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            } else if (selectedPairing != null) {
                // If disabled but has a selected pairing, show the teams
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    val description = selectedPairing.description
                    val teams = description.split(" vs ")

                    if (teams.size == 2) {
                        Text(
                            text = "Team 1: ${teams[0]}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Team 2: ${teams[1]}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}