package com.golfapp.gsa51.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.utils.HapticFeedback
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.ScoringViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScoringScreen(
    gameId: Long,
    onNavigateToResults: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToGameRules: () -> Unit
) {
    // Initialize the ViewModel
    val viewModel = viewModel<ScoringViewModel>(
        factory = AppViewModelProvider.Factory,
        key = "scoring_$gameId"
    )

    // Initialize the ViewModel with the game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Keep track of score inputs for each player
    val scoreInputs = remember { mutableStateMapOf<Long, String>() }

    val context = LocalContext.current

    // Par input state
    var parInput by remember { mutableStateOf("") }

    // Initialize from viewModel whenever holePar changes
    LaunchedEffect(viewModel.holePar) {
        parInput = viewModel.holePar?.toString() ?: ""
    }

    // Initialize score input fields from viewModel whenever scores change
    LaunchedEffect(viewModel.currentHole, viewModel.scores) {
        // Clear previous inputs when hole changes
        scoreInputs.clear()

        // Fill with current scores if available
        viewModel.players.forEach { player ->
            val currentScore = viewModel.scores[player.id]
            if (currentScore != null) {
                scoreInputs[player.id] = currentScore.toString()
            }
        }
    }

    // Focus requesters
    val parFieldFocus = remember { FocusRequester() }
    val firstPlayerScoreFocus = remember { FocusRequester() }

    // Request focus to appropriate field when screen loads or hole changes
    LaunchedEffect(viewModel.currentHole, viewModel.isParConfirmed) {
        delay(300) // Short delay to ensure UI is ready
        if (viewModel.isParConfirmed) {
            try {
                firstPlayerScoreFocus.requestFocus()
            } catch (e: Exception) { }
        } else {
            try {
                parFieldFocus.requestFocus()
            } catch (e: Exception) { }
        }
    }

    // Function to handle setting par
    val handleSetPar = {
        if (viewModel.confirmPar()) {
            try {
                firstPlayerScoreFocus.requestFocus()
            } catch (e: Exception) { }
        } else {
            viewModel.setError("Please enter a valid par value (3-5)")
        }
    }

    // State variables for dropdown menus
    var showParDropdown by remember { mutableStateOf(false) }
    var showGoToHoleDropdown by remember { mutableStateOf(false) }
    val playerDropdownVisible = remember { mutableStateMapOf<Long, Boolean>() }

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Scoring",
                showBackButton = true,
                onBackClick = {
                    if (viewModel.hasUnsavedChanges) {
                        viewModel.autoSave {
                            onNavigateBack()
                        }
                    } else {
                        onNavigateBack()
                    }
                },
                onInfoClick = onNavigateToGameRules
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing
        ) {
            // Always show the tooltip - regardless of hole
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD) // Light blue background
                    ),
                    border = BorderStroke(1.dp, GSAPurple.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Score Limit Info",
                            tint = GSAPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Add logging to debug tooltip text
                        val tooltipText = "Scores limited to ${viewModel.maxScoreLimit}. Change in Game Settings."
                        Log.d("ScoringUI", "Displaying tooltip with limit: ${viewModel.maxScoreLimit}")
                        Text(
                            text = tooltipText,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Current Hole Display
            Text(
                text = "Hole ${viewModel.currentHole}",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 2.dp)
            )

            // Go to hole row - with dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Go to hole:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                // Dropdown for Go to Hole
                Box(
                    modifier = Modifier.width(190.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { showGoToHoleDropdown = true }
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = viewModel.navigateToHoleInput.ifEmpty { "1-18" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (viewModel.navigateToHoleInput.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Hole",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showGoToHoleDropdown,
                        onDismissRequest = { showGoToHoleDropdown = false },
                        modifier = Modifier.width(190.dp),
                        properties = PopupProperties(focusable = true)
                    ) {
                        (1..18).forEach { hole ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = hole.toString(),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    viewModel.updateNavigateToHoleInput(hole.toString())
                                    showGoToHoleDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.navigateToHole(viewModel.navigateToHoleInput)
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .width(45.dp), // Makes the button just wide enough for "GO"
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp) // Reduce padding
                ) {
                    Text(
                        "GO",
                        fontSize = 14.sp, // Slightly smaller text
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp // Tighter letter spacing
                    )
                }
            }

            // Par row - with dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Par:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!viewModel.isParConfirmed) Color.Red else Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                // Dropdown for Par
                Box(
                    modifier = Modifier.width(190.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { showParDropdown = true }
                            .border(
                                width = 1.dp,
                                color = if (!viewModel.isParConfirmed) Color.Red else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = parInput.ifEmpty { "3-5" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (parInput.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Par",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showParDropdown,
                        onDismissRequest = { showParDropdown = false },
                        modifier = Modifier.width(190.dp),
                        properties = PopupProperties(focusable = true)
                    ) {
                        (3..5).forEach { parValue ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = parValue.toString(),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    parInput = parValue.toString()
                                    viewModel.updatePar(parValue.toString())
                                    showParDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { handleSetPar() },
                    modifier = Modifier
                        .height(56.dp)
                        .width(45.dp), // Makes the button just wide enough for "SET"
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isParConfirmed) Color.Green else GSAPurple
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp) // Reduce padding
                ) {
                    Text(
                        "SET",
                        fontSize = 14.sp, // Slightly smaller text
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp // Tighter letter spacing
                    )
                }
            }

            // Team Pairings info
            val (team1Player1, team1Player2) = viewModel.getTeamMemberNames(1)
            val (team2Player1, team2Player2) = viewModel.getTeamMemberNames(2)

            if (team1Player1.isNotEmpty() && team1Player2.isNotEmpty() && team2Player1.isNotEmpty() && team2Player2.isNotEmpty()) {
                Text(
                    text = "Team 1: $team1Player1 & $team1Player2  vs  Team 2: $team2Player1 & $team2Player2",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Player score entries with dropdowns
            viewModel.players.forEachIndexed { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEF6) // Light lavender background
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp
                        )

                        // Initialize dropdown state for this player if not already done
                        if (!playerDropdownVisible.containsKey(player.id)) {
                            playerDropdownVisible[player.id] = false
                        }

                        // Score dropdown for each player
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .then(
                                    if (index == 0)
                                        Modifier.focusRequester(firstPlayerScoreFocus)
                                    else
                                        Modifier
                                )
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable {
                                        playerDropdownVisible[player.id] = true
                                    }
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                shape = RoundedCornerShape(4.dp),
                                color = if ((scoreInputs[player.id] ?: "").isNotEmpty())
                                    Color(0xFFE8F5E9) // Light green for valid score
                                else
                                    MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = scoreInputs[player.id] ?: "#",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f),
                                        color = if ((scoreInputs[player.id] ?: "").isEmpty())
                                            Color.Gray
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Score",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Score dropdown menu
                            DropdownMenu(
                                expanded = playerDropdownVisible[player.id] == true,
                                onDismissRequest = { playerDropdownVisible[player.id] = false },
                                modifier = Modifier.width(70.dp),
                                properties = PopupProperties(focusable = true)
                            ) {
                                // Create score options from 1 to maxScoreLimit
                                (1..viewModel.maxScoreLimit).forEach { score ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = score.toString(),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        },
                                        onClick = {
                                            val newValue = score.toString()
                                            scoreInputs[player.id] = newValue
                                            viewModel.updateScore(player.id, newValue)
                                            playerDropdownVisible[player.id] = false

                                            // Move focus to next player if not the last one
                                            if (index < viewModel.players.size - 1) {
                                                focusManager.moveFocus(FocusDirection.Down)
                                            } else {
                                                keyboardController?.hide()
                                                viewModel.autoSave()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            // VIEW RESULTS button (full width)
            Button(
                onClick = {
                    if (viewModel.allHolesScored) {
                        onNavigateToResults(gameId)
                    } else {
                        viewModel.setError("Enter score for all 18 holes")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GSAPurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "VIEW RESULTS",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // PREVIOUS, SAVE GAME, NEXT buttons in a single row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // PREVIOUS button
                OutlinedButton(
                    onClick = {
                        if (viewModel.hasUnsavedChanges) {
                            viewModel.autoSave {
                                viewModel.previousHole()
                            }
                        } else {
                            viewModel.previousHole()
                        }
                    },
                    modifier = Modifier
                        .weight(0.33f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GSAPurple
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GSAPurple)
                ) {
                    Text(
                        text = "PREVIOUS",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // SAVE GAME button (in the middle)
                Button(
                    onClick = {
                        HapticFeedback.performMediumClick(context)
                        viewModel.saveScores()
                    },
                    modifier = Modifier
                        .weight(0.34f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SAVE GAME",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                // NEXT button
                Button(
                    onClick = {
                        if (viewModel.hasUnsavedChanges) {
                            viewModel.autoSave {
                                viewModel.nextHole()
                            }
                        } else {
                            viewModel.nextHole()
                        }
                    },
                    modifier = Modifier
                        .weight(0.33f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GSAPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "NEXT",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
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

    // Error message dialog
    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(viewModel.errorMessage ?: "") },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}