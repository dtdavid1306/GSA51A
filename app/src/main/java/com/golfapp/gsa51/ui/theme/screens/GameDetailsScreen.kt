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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.GameDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Game Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToGameRules() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Game Rules",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = GSAPurple
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Game Details",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Location
            Text(
                text = "Location",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = viewModel.location,
                onValueChange = { viewModel.updateLocation(it) },
                placeholder = { Text("Enter location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Date
            Text(
                text = "Date",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = if (viewModel.gameDate.time > 0)
                    SimpleDateFormat(
                        "MMMM dd, yyyy",
                        Locale.getDefault()
                    ).format(viewModel.gameDate)
                else "",
                onValueChange = { },
                placeholder = { Text("Select date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Select Date",
                            tint = GSAPurple
                        )
                    }
                }
            )

            // Bet Unit and Starting Hole in separate rows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Bet Unit - SIMPLIFIED
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Bet Unit",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextField(
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
                        placeholder = { Text("$ enter bet unit") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = GSAPurple,
                            cursorColor = GSAPurple
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Starting Hole - SIMPLIFIED
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Starting Hole",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(
                            onClick = { /* Show info about starting hole */ },
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Starting Hole Info",
                                modifier = Modifier.size(14.dp),
                                tint = GSAPurple
                            )
                        }
                    }
                    TextField(
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
                        placeholder = { Text("Enter starting hole") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = GSAPurple,
                            cursorColor = GSAPurple
                        ),
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

            // Players Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            ) {
                Text(
                    text = "Players",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { /* Show player info */ },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Player Info",
                        modifier = Modifier.size(14.dp),
                        tint = GSAPurple
                    )
                }
            }

            // Player 1
            Text(
                text = "Player 1",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = viewModel.player1Name,
                onValueChange = { viewModel.updatePlayer1Name(it) },
                placeholder = { Text("Enter player name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = viewModel.player2Name,
                onValueChange = { viewModel.updatePlayer2Name(it) },
                placeholder = { Text("Enter player name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = viewModel.player3Name,
                onValueChange = { viewModel.updatePlayer3Name(it) },
                placeholder = { Text("Enter player name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            TextField(
                value = viewModel.player4Name,
                onValueChange = { viewModel.updatePlayer4Name(it) },
                placeholder = { Text("Enter player name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = GSAPurple,
                    cursorColor = GSAPurple
                ),
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

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { onNavigateToSavedGames() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GSAPurple
                    )
                ) {
                    Text("LOAD GAME", style = MaterialTheme.typography.labelLarge)
                }

                // In GameDetailsScreen.kt, find the Button section for "START GAME" and update it:

                Button(
                    onClick = {
                        scope.launch {
                            // Only proceed if validation passes
                            if (viewModel.player1Name.isNotBlank() &&
                                viewModel.player2Name.isNotBlank() &&
                                viewModel.player3Name.isNotBlank() &&
                                viewModel.player4Name.isNotBlank() &&
                                viewModel.location.isNotBlank()) {

                                // Create the game and get the game ID
                                val gameId = viewModel.createGame()

                                // Navigate to Individual Game Settings with the game ID
                                onNavigateToIndividualSettings(gameId)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = viewModel.player1Name.isNotBlank() &&
                            viewModel.player2Name.isNotBlank() &&
                            viewModel.player3Name.isNotBlank() &&
                            viewModel.player4Name.isNotBlank() &&
                            viewModel.location.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GSAPurple
                    )
                ) {
                    Text("START GAME", style = MaterialTheme.typography.labelLarge)
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
    }
}