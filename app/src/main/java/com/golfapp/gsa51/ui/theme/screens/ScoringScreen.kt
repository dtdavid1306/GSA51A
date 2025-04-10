package com.golfapp.gsa51.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.ScoringViewModel
import kotlinx.coroutines.delay
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.ui.theme.components.GSAScoreField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.golfapp.gsa51.R
import com.golfapp.gsa51.utils.HapticFeedback
import androidx.compose.ui.platform.LocalContext


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
                        val tooltipText = "Scores limited to ${viewModel.maxScoreLimit}. Change in Advanced Settings."
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

            // Go to hole row - with wider button
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

                OutlinedTextField(
                    value = viewModel.navigateToHoleInput,
                    onValueChange = { viewModel.updateNavigateToHoleInput(it) },
                    placeholder = {
                        Text(
                            "1-18",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier
                        .width(190.dp) // Slightly narrower text field
                        .height(56.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.navigateToHole(viewModel.navigateToHoleInput)
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = GSAPurple,
                        cursorColor = GSAPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

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

// Par row - with wider button
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

                OutlinedTextField(
                    value = parInput,
                    onValueChange = {
                        parInput = it
                        viewModel.updatePar(it)
                    },
                    placeholder = {
                        Text(
                            "3-5",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier
                        .width(190.dp) // Slightly narrower text field
                        .height(56.dp)
                        .focusRequester(parFieldFocus),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { handleSetPar() }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = if (!viewModel.isParConfirmed) Color.Red else GSAPurple,
                        cursorColor = GSAPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

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

            // Player score entries - Compact
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

                        GSAScoreField(
                            value = scoreInputs[player.id] ?: "",
                            onValueChange = { newValue ->
                                // Check if the new value is a valid number and within the limit
                                val score = newValue.toIntOrNull()
                                if (score != null && score > viewModel.maxScoreLimit) {
                                    // If invalid, clear the field completely and show error
                                    scoreInputs[player.id] = "" // Clear the entire field
                                    viewModel.setError("Score cannot exceed ${viewModel.maxScoreLimit}. Please enter a value between 1 and ${viewModel.maxScoreLimit}.")
                                } else {
                                    // If valid or empty, update normally
                                    scoreInputs[player.id] = newValue
                                    viewModel.updateScore(player.id, newValue)
                                }
                            },
                            modifier = Modifier
                                .width(70.dp)
                                .then(
                                    if (index == 0)
                                        Modifier.focusRequester(firstPlayerScoreFocus)
                                    else
                                        Modifier
                                ),
                            keyboardActions = if (index < viewModel.players.size - 1) {
                                KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                            } else {
                                KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        viewModel.autoSave()
                                    }
                                )
                            }
                        )
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