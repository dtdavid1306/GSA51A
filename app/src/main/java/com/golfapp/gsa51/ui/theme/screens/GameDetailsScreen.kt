package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.GameDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.ui.theme.components.GSATextField
import com.golfapp.gsa51.ui.theme.components.GSAPrimaryButton
import com.golfapp.gsa51.ui.theme.components.GSASecondaryButton
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.Tooltip

// Define the GSA purple color
val GSAPurple = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GameDetailsScreen(
    onNavigateToIndividualSettings: (Long) -> Unit = {},
    onNavigateToGameRules: () -> Unit = {},
    onNavigateToSavedGames: () -> Unit = {},
    onExitApp: () -> Unit = {},
    viewModel: GameDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showStartingHoleTooltip by remember { mutableStateOf(false) }
    var showPlayersTooltip by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Game Details",
                showBackButton = true,
                onBackClick = {
                    showExitDialog = true
                },
                onInfoClick = onNavigateToGameRules
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
        ) {
            // Location and Date in the same row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Location
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                    GSATextField(
                        value = viewModel.location,
                        onValueChange = { viewModel.updateLocation(it) },
                        placeholder = "Enter location",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Increased height
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        )
                    )
                }

                // Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                    OutlinedTextField(
                        value = if (viewModel.gameDate.time > 0)
                            SimpleDateFormat(
                                "MMM d, yyyy",
                                Locale.getDefault()
                            ).format(viewModel.gameDate)
                        else "",
                        onValueChange = { },
                        placeholder = { Text("Select date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Increased height
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = "Select Date",
                                    tint = GSAPurple
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = GSAPurple,
                            cursorColor = GSAPurple
                        ),
                        singleLine = true
                    )
                }
            }

            // Bet Unit and Starting Hole in separate row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bet Unit
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = "Bet Unit",
                        style = MaterialTheme.typography.titleMedium
                    )
                    GSATextField(
                        value = if (viewModel.betUnit == 0) "" else viewModel.betUnit.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                viewModel.updateBetUnit(0)
                            } else {
                                it.toIntOrNull()?.let { value ->
                                    viewModel.updateBetUnit(value)
                                }
                            }
                        },
                        placeholder = "$ enter bet unit",
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Increased height
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        )
                    )
                }

                // Starting Hole
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Starting Hole",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = { showStartingHoleTooltip = true },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Starting Hole Info",
                                modifier = Modifier.size(12.dp),
                                tint = GSAPurple
                            )
                        }
                    }

                    GSATextField(
                        value = if (viewModel.startingHole == 0) "" else viewModel.startingHole.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                viewModel.updateStartingHole(0)
                            } else {
                                it.toIntOrNull()?.let { value ->
                                    if (value in 1..18) {
                                        viewModel.updateStartingHole(value)
                                    }
                                }
                            }
                        },
                        placeholder = "Enter starting hole",
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Increased height
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            }

            // Players Section with more compact design
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp, bottom = 1.dp)
            ) {
                Text(
                    text = "Players",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { showPlayersTooltip = true },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Player Info",
                        modifier = Modifier.size(12.dp),
                        tint = GSAPurple
                    )
                }
            }

            // Player 1 & 2 in the same row
            Text(
                text = "Player 1",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            GSATextField(
                value = viewModel.player1Name,
                onValueChange = { viewModel.updatePlayer1Name(it) },
                placeholder = "Enter name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Player 2
            Text(
                text = "Player 2",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            GSATextField(
                value = viewModel.player2Name,
                onValueChange = { viewModel.updatePlayer2Name(it) },
                placeholder = "Enter name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Player 3
            Text(
                text = "Player 3",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            GSATextField(
                value = viewModel.player3Name,
                onValueChange = { viewModel.updatePlayer3Name(it) },
                placeholder = "Enter name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Player 4
            Text(
                text = "Player 4",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            GSATextField(
                value = viewModel.player4Name,
                onValueChange = { viewModel.updatePlayer4Name(it) },
                placeholder = "Enter name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Increased height
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.weight(1f, fill = true))

            // Buttons in a row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GSASecondaryButton(
                    text = "LOAD GAME",
                    onClick = { onNavigateToSavedGames() },
                    modifier = Modifier.weight(1f)
                )

                GSAPrimaryButton(
                    text = "START GAME",
                    onClick = {
                        scope.launch {
                            if (viewModel.player1Name.isNotBlank() &&
                                viewModel.player2Name.isNotBlank() &&
                                viewModel.player3Name.isNotBlank() &&
                                viewModel.player4Name.isNotBlank() &&
                                viewModel.location.isNotBlank()) {

                                val gameId = viewModel.createGame()
                                onNavigateToIndividualSettings(gameId)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = viewModel.player1Name.isNotBlank() &&
                            viewModel.player2Name.isNotBlank() &&
                            viewModel.player3Name.isNotBlank() &&
                            viewModel.player4Name.isNotBlank() &&
                            viewModel.location.isNotBlank()
                )
            }

            // Date picker dialog
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = viewModel.gameDate.time
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    viewModel.updateGameDate(Date(millis))
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Exit confirmation dialog
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Exit Application") },
                    text = { Text("Do you want to exit GSA?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showExitDialog = false
                                onExitApp()
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
                            onClick = { showExitDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = GSAPurple
                            )
                        ) {
                            Text("NO")
                        }
                    }
                )
            }

            if (showStartingHoleTooltip) {
                Tooltip(
                    text = "The starting hole determines the rotation of team pairings. Each pairing plays for 6 holes.",
                    onDismissRequest = { showStartingHoleTooltip = false }
                )
            }

            if (showPlayersTooltip) {
                Tooltip(
                    text = "Enter the names of all four players for this golf game.",
                    onDismissRequest = { showPlayersTooltip = false }
                )
            }
        }
    }
}