package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.ScoringViewModel
import kotlinx.coroutines.delay
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar

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

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Keep track of score inputs for each player
    val scoreInputs = remember { mutableStateMapOf<Long, String>() }

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
            // If par is already confirmed, focus on first player
            try {
                firstPlayerScoreFocus.requestFocus()
            } catch (e: Exception) {
                // Handle any focus errors
            }
        } else {
            // If par needs to be set, focus on par field
            try {
                parFieldFocus.requestFocus()
            } catch (e: Exception) {
                // Handle any focus errors
            }
        }
    }

    // Function to handle setting par
    val handleSetPar = {
        if (viewModel.confirmPar()) {
            // Focus on first player's score field
            try {
                firstPlayerScoreFocus.requestFocus()
            } catch (e: Exception) {
                // Handle any focus errors
            }
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
                        // Auto-save before navigating back
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
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Current Hole Display
            Text(
                text = "Hole ${viewModel.currentHole}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Go to Hole and Par row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Go to hole
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Go to hole:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    TextField(
                        value = viewModel.navigateToHoleInput,
                        onValueChange = { viewModel.updateNavigateToHoleInput(it) },
                        placeholder = { Text("1-18") },
                        modifier = Modifier.width(60.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                // Check for par before navigating
                                if (viewModel.isParValid()) {
                                    if (viewModel.hasUnsavedChanges) {
                                        viewModel.autoSave {
                                            viewModel.navigateToHole(viewModel.navigateToHoleInput)
                                        }
                                    } else {
                                        viewModel.navigateToHole(viewModel.navigateToHoleInput)
                                    }
                                } else {
                                    viewModel.setError("Please enter a par value (3-5)")
                                }
                            }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = GSAPurple,
                            cursorColor = GSAPurple
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = {
                            // Check for par before navigating
                            if (viewModel.isParValid()) {
                                if (viewModel.hasUnsavedChanges) {
                                    viewModel.autoSave {
                                        viewModel.navigateToHole(viewModel.navigateToHoleInput)
                                    }
                                } else {
                                    viewModel.navigateToHole(viewModel.navigateToHoleInput)
                                }
                            } else {
                                viewModel.setError("Please enter a par value (3-5)")
                            }
                        },
                        modifier = Modifier.height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                    ) {
                        Text("GO")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Par for hole section - with focus indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Par:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (!viewModel.isParConfirmed) Color.Red else Color.Black,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    TextField(
                        value = parInput,
                        onValueChange = {
                            parInput = it
                            viewModel.updatePar(it)
                        },
                        placeholder = { Text("3-5") },
                        modifier = Modifier
                            .width(60.dp)
                            .focusRequester(parFieldFocus),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { handleSetPar() }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = if (!viewModel.isParConfirmed) Color.Red else GSAPurple,
                            cursorColor = GSAPurple
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = { handleSetPar() },
                        modifier = Modifier.height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.isParConfirmed) Color.Green else GSAPurple
                        )
                    ) {
                        Text("SET")
                    }
                }
            }

            // Team Pairings for current hole
            val (team1Player1, team1Player2) = viewModel.getTeamMemberNames(1)
            val (team2Player1, team2Player2) = viewModel.getTeamMemberNames(2)

            if (team1Player1.isNotEmpty() && team1Player2.isNotEmpty() && team2Player1.isNotEmpty() && team2Player2.isNotEmpty()) {
                Text(
                    text = "Team 1: $team1Player1 & $team1Player2  vs  Team 2: $team2Player1 & $team2Player2",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Player score entries
            viewModel.players.forEachIndexed { index, player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextField(
                            value = scoreInputs[player.id] ?: "",
                            onValueChange = {
                                scoreInputs[player.id] = it
                                viewModel.updateScore(player.id, it)
                            },
                            placeholder = { Text("Score") },
                            modifier = Modifier
                                .width(80.dp)
                                .then(
                                    if (index == 0)
                                        Modifier.focusRequester(firstPlayerScoreFocus)
                                    else
                                        Modifier
                                ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = if (player == viewModel.players.lastOrNull())
                                    ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    keyboardController?.hide()
                                    // Auto-save when done is pressed on last player
                                    viewModel.autoSave()
                                }
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = GSAPurple,
                                cursorColor = GSAPurple
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Button(
                onClick = { viewModel.saveScores() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
            ) {
                Text("SAVE GAME")
            }

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
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.allHolesScored) Color.Green else GSAPurple
                )
            ) {
                Text("VIEW RESULTS")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.previousHole() },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("PREVIOUS")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.nextHole() },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("NEXT")
                }
            }
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