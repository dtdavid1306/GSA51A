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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.GameDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.golfapp.gsa51.ui.navigation.Screen
import kotlinx.coroutines.launch
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar
import com.golfapp.gsa51.ui.theme.components.GSATextField
import com.golfapp.gsa51.ui.theme.components.GSAPrimaryButton
import com.golfapp.gsa51.ui.theme.components.GSASecondaryButton
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.Tooltip
import androidx.compose.ui.platform.LocalContext
import com.golfapp.gsa51.utils.HapticFeedback

// Define the GSA purple color
val GSAPurple = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GameDetailsScreen(
    onNavigateToIndividualSettings: (Long) -> Unit = {},
    onNavigateToGameRules: () -> Unit = {},
    onNavigateToSavedGames: () -> Unit = {},
    onNavigateToAdvancedSettings: () -> Unit = {}, // Add this parameter
    onExitApp: () -> Unit = {},
    viewModel: GameDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showStartingHoleTooltip by remember { mutableStateOf(false) }
    var showPlayersTooltip by remember { mutableStateOf(false) }
    var showValidationMessage by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Game Details",
                showBackButton = true,  // Show back button
                onBackClick = {
                    // Show confirmation dialog before exiting
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main content column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Location and Date in the same row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Location field
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        GSATextField(
                            value = viewModel.location,
                            onValueChange = { viewModel.updateLocation(it) },
                            placeholder = "Enter location",
                            modifier = Modifier.height(56.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) })
                        )
                    }

                    // Date field
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = if (viewModel.gameDate.time > 0)
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(viewModel.gameDate)
                            else "",
                            onValueChange = { },
                            placeholder = { Text("Select date") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
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
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                    }
                }

                // Bet Unit and Starting Hole row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Bet Unit
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Bet Unit",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // For GSA_LOGO we need a custom field with the logo
                        if (viewModel.currencySymbol == "GSA_LOGO") {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // The main text field
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
                                    placeholder = "enter bet unit",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(start = 40.dp), // Make room for the logo
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) })
                                )

                                // GSA Logo placeholder - replace with actual logo when available
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 12.dp)
                                        .size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "GSA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GSAPurple
                                    )
                                    // Uncomment when the real logo is available
                                    /*
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_gsa_logo),
                                        contentDescription = "GSA Logo",
                                        tint = GSAPurple,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    */
                                }
                            }
                        } else {
                            // Regular currency symbol text field
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
                                placeholder = "${viewModel.currencySymbol} enter bet unit",
                                modifier = Modifier.height(56.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) })
                            )
                        }
                    }

                    // Starting Hole with tooltip
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Starting Hole",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = { showStartingHoleTooltip = true },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = "Starting Hole Info",
                                    modifier = Modifier.size(18.dp),
                                    tint = GSAPurple
                                )
                            }
                        }

                        if (showStartingHoleTooltip) {
                            Tooltip(
                                text = "The starting hole determines the rotation of team pairings. Each pairing plays for 6 holes, and the sequence begins from the starting hole you select.",
                                onDismissRequest = { showStartingHoleTooltip = false }
                            )
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
                            modifier = Modifier.height(56.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                    }
                }

                // Player 1 with tooltip
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Player 1",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = { showPlayersTooltip = true },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Player Info",
                                modifier = Modifier.size(18.dp),
                                tint = GSAPurple
                            )
                        }
                    }

                    if (showPlayersTooltip) {
                        Tooltip(
                            text = "Enter the names of all four players. These names will be used throughout the game for scoring and team pairings.",
                            onDismissRequest = { showPlayersTooltip = false }
                        )
                    }

                    GSATextField(
                        value = viewModel.player1Name,
                        onValueChange = { viewModel.updatePlayer1Name(it) },
                        placeholder = "Enter name",
                        modifier = Modifier.height(56.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                // Player 2
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Player 2",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    GSATextField(
                        value = viewModel.player2Name,
                        onValueChange = { viewModel.updatePlayer2Name(it) },
                        placeholder = "Enter name",
                        modifier = Modifier.height(56.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                // Player 3
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Player 3",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    GSATextField(
                        value = viewModel.player3Name,
                        onValueChange = { viewModel.updatePlayer3Name(it) },
                        placeholder = "Enter name",
                        modifier = Modifier.height(56.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                }

                // Player 4
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Player 4",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    GSATextField(
                        value = viewModel.player4Name,
                        onValueChange = { viewModel.updatePlayer4Name(it) },
                        placeholder = "Enter name",
                        modifier = Modifier.height(56.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    )
                }
            }

            // Validation message
            if (showValidationMessage) {
                Text(
                    text = "Please fill all fields",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GSASecondaryButton(
                    text = "LOAD GAME",
                    onClick = { onNavigateToSavedGames() },
                    modifier = Modifier.weight(1f)
                )

                // Check if form is valid
                val isFormValid = viewModel.player1Name.isNotBlank() &&
                        viewModel.player2Name.isNotBlank() &&
                        viewModel.player3Name.isNotBlank() &&
                        viewModel.player4Name.isNotBlank() &&
                        viewModel.location.isNotBlank()

                // Use custom button colors
                val buttonColor = if (isFormValid) Color(0xFF4CAF50) else GSAPurple

                Button(
                    onClick = {
                        if (isFormValid) {
                            showValidationMessage = false
                            scope.launch {
                                val gameId = viewModel.createGame()
                                onNavigateToIndividualSettings(gameId)
                            }
                        } else {
                            // Show validation message
                            showValidationMessage = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        disabledContainerColor = buttonColor.copy(alpha = 0.5f)
                    ),
                    enabled = true // Always enabled to show validation message
                ) {
                    Text(
                        text = "START GAME",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Add this AFTER the existing buttons Row - still inside the main Column
            Spacer(modifier = Modifier.height(8.dp))

            // Row for NEW GAME and GAME SETTINGS buttons side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // New Game button - on the left
                OutlinedButton(
                    onClick = {
                        // Call the reset fields method and add haptic feedback
                        viewModel.resetFields()
                        HapticFeedback.performLightClick(context)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GSAPurple.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "NEW GAME",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp
                    )
                }

                // Game Settings button - renamed from "ADVANCED SETTINGS"
                OutlinedButton(
                    onClick = {
                        onNavigateToAdvancedSettings()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GSAPurple.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "GAME SETTINGS",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp
                    )
                }
            }

            // The closing bracket for the main Column should be here
        }

        // Date picker dialog - This should be outside the main Column
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
                            onExitApp() // Call the exit function
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
    }
}