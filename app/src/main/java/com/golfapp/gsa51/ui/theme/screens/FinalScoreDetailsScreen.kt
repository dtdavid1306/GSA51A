package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.R
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.FinalScoreDetailsViewModel
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalScoreDetailsScreen(
    gameId: Long,
    onNavigateBack: () -> Unit,
    viewModel: FinalScoreDetailsViewModel = viewModel(
        factory = AppViewModelProvider.Factory,
        key = "finalScoreDetails_$gameId"
    )
) {
    // Initialize ViewModel with game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Final Score Details", color = Color.White) },
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
                    .padding(horizontal = 8.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Final Score Details",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )

                // Legend Row
                ScoreLegend()

                // Scores Table Header
                ScoresTableHeader(viewModel)

                // Scores Table Rows
                for (holeNumber in 1..18) {
                    ScoreTableRow(holeNumber, viewModel)
                }

                // Total Row
                TotalScoreRow(viewModel)

                // Back to Results Button
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
                ) {
                    Text("BACK TO RESULTS")
                }
            }
        }
    }
}

@Composable
fun ScoreLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Legend:",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp, // CHANGE: Smaller font size
            modifier = Modifier.padding(end = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LegendItem("Eagle+", Color(0xFF388E3C))
            LegendItem("Birdie", Color(0xFF7CB342))
            LegendItem("Par", Color(0xFF2196F3))
            LegendItem("Bogey", Color(0xFFF44336))
            LegendItem("Double+", Color(0xFFD32F2F))
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Surface(
        modifier = Modifier.padding(end = 2.dp), // CHANGE: Reduced padding
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), // CHANGE: Reduced padding
            style = MaterialTheme.typography.labelMedium,
            fontSize = 10.sp // CHANGE: Smaller font size
        )
    }
}

@Composable
fun ScoresTableHeader(viewModel: FinalScoreDetailsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GSAPurple) // CHANGE: Use GSAPurple instead of generic purple
            .padding(2.dp) // CHANGE: Reduced from 4.dp to 2.dp
    ) {
        // Hole column
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hole",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Par column
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Par",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Player name columns
        val playerCellWidth = 0.7f / viewModel.players.size
        viewModel.players.forEach { player ->
            Box(
                modifier = Modifier
                    .weight(playerCellWidth)
                    .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp // CHANGE: Added smaller font size
                )
            }
        }
    }
}

@Composable
fun ScoreTableRow(holeNumber: Int, viewModel: FinalScoreDetailsViewModel) {
    val holeScores = viewModel.scoresByHole[holeNumber] ?: emptyMap()

    // Get the par for this hole
    val par = holeScores.values.firstOrNull()?.par ?: 4 // Default to par 4 if not available

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (holeNumber % 2 == 0) Color(0xFFF5F5F5) else Color.White)
            .padding(2.dp) // CHANGE: Reduced from 4.dp to 2.dp
    ) {
        // Hole column - make more compact
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hole $holeNumber",
                color = Color(0xFF616161),
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Par column - make more compact
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = par.toString(),
                color = Color(0xFF616161),
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Player score columns - make more compact
        val playerCellWidth = 0.7f / viewModel.players.size
        viewModel.players.forEach { player ->
            val score = holeScores[player.id]
            if (score != null) {
                val colorType = viewModel.getScoreColorType(score.score, score.par)
                val backgroundColor = when (colorType) {
                    FinalScoreDetailsViewModel.ScoreColorType.EAGLE_PLUS -> Color(0xFF388E3C)
                    FinalScoreDetailsViewModel.ScoreColorType.BIRDIE -> Color(0xFF7CB342)
                    FinalScoreDetailsViewModel.ScoreColorType.PAR -> Color(0xFF2196F3)
                    FinalScoreDetailsViewModel.ScoreColorType.BOGEY -> Color(0xFFF44336)
                    FinalScoreDetailsViewModel.ScoreColorType.DOUBLE_PLUS -> Color(0xFFD32F2F)
                }

                Box(
                    modifier = Modifier
                        .weight(playerCellWidth)
                        .background(backgroundColor)
                        .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = score.score.toString(),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp // CHANGE: Added smaller font size
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(playerCellWidth)
                        .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp // CHANGE: Added smaller font size
                    )
                }
            }
        }
    }
}

@Composable
fun TotalScoreRow(viewModel: FinalScoreDetailsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(2.dp) // CHANGE: Reduced from 4.dp to 2.dp
    ) {
        // Total label
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Total",
                color = Color(0xFF212121),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Par total placeholder
        Box(
            modifier = Modifier
                .weight(0.15f)
                .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                color = Color(0xFF212121),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp // CHANGE: Added smaller font size
            )
        }

        // Player total scores
        val playerCellWidth = 0.7f / viewModel.players.size
        viewModel.players.forEach { player ->
            val totalScore = viewModel.totalScores[player.id] ?: 0
            Box(
                modifier = Modifier
                    .weight(playerCellWidth)
                    .padding(2.dp), // CHANGE: Reduced from 4.dp to 2.dp
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = totalScore.toString(),
                    color = Color(0xFF212121),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp // CHANGE: Added smaller font size
                )
            }
        }
    }
}