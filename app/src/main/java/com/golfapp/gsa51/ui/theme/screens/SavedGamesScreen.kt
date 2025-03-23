package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.SavedGamesViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGamesScreen(
    onNavigateBack: () -> Unit,
    onResumeGame: (Long) -> Unit,
    onNewGame: () -> Unit,
    onNavigateToGameRules: () -> Unit,
    viewModel: SavedGamesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Saved Games",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onInfoClick = onNavigateToGameRules
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                // Loading state
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = GSAPurple
                )
            } else if (viewModel.games.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No saved games found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNewGame,
                        colors = ButtonDefaults.buttonColors(containerColor = GSAPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("START NEW GAME")
                    }
                }
            } else {
                // Games list
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Saved Games",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.games) { gameItem ->
                            GameCard(
                                gameItem = gameItem,
                                onResume = { onResumeGame(gameItem.game.id) },
                                onDelete = { viewModel.confirmDelete(gameItem.game) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNewGame,
                        colors = ButtonDefaults.buttonColors(containerColor = GSAPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("NEW GAME")
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (viewModel.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Delete Game") },
            text = { Text("Are you sure you want to delete this game? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteGame() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("DELETE")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDelete() }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun GameCard(
    gameItem: SavedGamesViewModel.SavedGameItem,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Game location
            Text(
                text = gameItem.game.location,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Game date
            Text(
                text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                    .format(gameItem.game.date),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Player names
            Text(
                text = gameItem.playerNames,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Current hole
            Text(
                text = "Current Hole: ${gameItem.game.currentHole}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onResume,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("RESUME")
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Red
                ) {
                    Text("DELETE")
                }
            }
        }
    }
}