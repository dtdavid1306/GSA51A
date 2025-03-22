package com.golfapp.gsa51.ui.theme.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.ResultsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    gameId: Long,
    onNavigateToScoreDetails: (Long) -> Unit,
    onNavigateToNewGame: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ResultsViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        key = "results_$gameId"
    )
) {
    // Initialize ViewModel with game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    // Add scroll state
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Results", color = Color.White) },
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
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GSAPurple)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Results",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Add starting hole information
                viewModel.game?.let { game ->
                    Text(
                        text = "Team Pairings: Based on Starting Hole ${game.startingHole}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = GSAPurple
                    )
                }

                // Game Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Game Information",
                            style = MaterialTheme.typography.titleMedium
                        )

                        viewModel.game?.let { game ->
                            Text("Location: ${game.location}")
                            Text("Date: ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(game.date)}")
                            Text("Bet Value: $${game.betUnit}")
                        }
                    }
                }

                // Player results
                viewModel.playerResults.values.forEach { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = result.player.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Total Score: ${result.totalScore}")

                            // Only show individual game results if player participated
                            if (result.player.participateInIndividualGame) {
                                Text("Individual Game: W${result.individualWins} D${result.individualDraws} L${result.individualLosses}")
                                Text("Individual Game Total: $${String.format("%.2f", result.individualWinnings)}")
                            }

                            Text("Team Game: W${result.teamWins} D${result.teamDraws} L${result.teamLosses}")
                            Text("Team Game Total: $${String.format("%.2f", result.teamWinnings)}")

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Combined Total: $${String.format("%.2f", result.combinedWinnings)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Action buttons
                Button(
                    onClick = {
                        // Simple share implementation
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Golf Score Report")
                            putExtra(Intent.EXTRA_TEXT, viewModel.generateShareableReport())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("SHARE COMPLETE REPORT")
                }

                Button(
                    onClick = { onNavigateToScoreDetails(gameId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("VIEW SCORE DETAILS")
                }

                Button(
                    onClick = onNavigateToNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("NEW GAME")
                }
            }
        }
    }
}