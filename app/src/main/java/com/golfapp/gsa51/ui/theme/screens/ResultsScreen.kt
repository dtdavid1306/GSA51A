package com.golfapp.gsa51.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.utils.ScreenshotUtil
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.FinalScoreDetailsViewModel
import com.golfapp.gsa51.viewmodels.ResultsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    gameId: Long,
    onNavigateToScoreDetails: (Long) -> Unit,
    onNavigateToNewGame: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToGameRules: () -> Unit,
    viewModel: ResultsViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        key = "results_$gameId"
    )
) {
    // Initialize ViewModel with game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    var showShareConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Get the FinalScoreDetailsViewModel at the Composable function level
    val scoreDetailsViewModel: FinalScoreDetailsViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        key = "finalScoreDetails_${gameId}"
    )

    // Initialize the ViewModel with the same gameId
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
        scoreDetailsViewModel.initialize(gameId)
    }

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Results",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onInfoClick = onNavigateToGameRules
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
                            Text(
                                "Date: ${
                                    SimpleDateFormat(
                                        "MMMM dd, yyyy",
                                        Locale.getDefault()
                                    ).format(game.date)
                                }"
                            )
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
                                Text(
                                    "Individual Game Total: $${
                                        String.format(
                                            "%.2f",
                                            result.individualWinnings
                                        )
                                    }"
                                )
                            }

                            Text("Team Game: W${result.teamWins} D${result.teamDraws} L${result.teamLosses}")
                            Text("Team Game Total: $${String.format("%.2f", result.teamWinnings)}")

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Combined Total: $${
                                    String.format(
                                        "%.2f",
                                        result.combinedWinnings
                                    )
                                }",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Action buttons
                Button(
                    onClick = { showShareConfirmDialog = true },
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

            // Show sharing progress overlay if needed
            if (viewModel.isSharing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "Preparing report...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Share confirmation dialog
        if (showShareConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showShareConfirmDialog = false },
                title = { Text("Share Report") },
                text = { Text("Share the complete game report including scorecard?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showShareConfirmDialog = false

                            // Show loading indicator while preparing the share content
                            viewModel.setSharing(true)

                            // Get the text report
                            val textReport = viewModel.generateShareableReport()

                            // Use a coroutine to wait for data to load before taking screenshot
                            lifecycleOwner.lifecycleScope.launch {
                                try {
                                    // Navigate to the score details screen
                                    onNavigateToScoreDetails(gameId)

                                    // Short delay to ensure the view is rendered
                                    delay(500)

                                    // Find the view to capture (root view of the activity)
                                    val rootView = (context as? Activity)?.window?.decorView

                                    var imageUri: Uri? = null
                                    if (rootView != null) {
                                        // Capture the entire screen
                                        imageUri = ScreenshotUtil.captureViewToUri(
                                            context = context,
                                            view = rootView.findViewById(android.R.id.content),
                                            filename = "scorecard_${gameId}.png"
                                        )
                                    } else {
                                        // Fallback to basic image if we can't get the view
                                        imageUri = ScreenshotUtil.createBasicImage(
                                            context = context,
                                            filename = "scorecard_${gameId}.png"
                                        )
                                    }

                                    // Navigate back to results screen
                                    onNavigateBack()

                                    // Share both text and image
                                    if (imageUri != null) {
                                        // Create a multi-part share intent
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND_MULTIPLE
                                            putExtra(Intent.EXTRA_SUBJECT, "Golf Score Report")

                                            // Put text report first for better visibility
                                            putExtra(Intent.EXTRA_TEXT, textReport)

                                            // Add the image URI
                                            val imageUris = ArrayList<Uri>()
                                            imageUris.add(imageUri)
                                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                                            type = "image/*"
                                        }

                                        context.startActivity(
                                            Intent.createChooser(
                                                shareIntent,
                                                "Share via"
                                            )
                                        )
                                    } else {
                                        // Fallback to text-only if image capture failed
                                        val textOnlyIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "Golf Score Report")
                                            putExtra(Intent.EXTRA_TEXT, textReport)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                textOnlyIntent,
                                                "Share via"
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("ResultsScreen", "Error during sharing", e)

                                    // Fallback to text-only sharing on error
                                    val textOnlyIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Golf Score Report")
                                        putExtra(Intent.EXTRA_TEXT, textReport)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            textOnlyIntent,
                                            "Share via"
                                        )
                                    )
                                } finally {
                                    // Hide loading indicator
                                    viewModel.setSharing(false)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                    ) {
                        Text("Share")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showShareConfirmDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}