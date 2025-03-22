package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.viewmodels.AppViewModelProvider
import com.golfapp.gsa51.viewmodels.IndividualGameSettingsViewModel
// Add this import at the top of IndividualGameSettingsScreen.kt
import com.golfapp.gsa51.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualGameSettingsScreen(
    gameId: Long,
    onNavigateToTeamPairings: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Create the ViewModel with the game ID
    val viewModel = viewModel<IndividualGameSettingsViewModel>(
        factory = AppViewModelProvider.Factory,
        // Create a new ViewModel instance for each game ID
        key = "individualGameSettings_$gameId"
    )

    // Initialize the ViewModel with the game ID
    LaunchedEffect(key1 = gameId) {
        viewModel.initialize(gameId)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GSA21", color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Individual Game Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Select which players will participate in the individual betting game. Players who opt-out will still participate in team games.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Player list with toggles
            viewModel.players.forEach { player ->
                PlayerParticipationCard(
                    player = player,
                    onToggleChange = { participates ->
                        viewModel.updatePlayerParticipation(player.id, participates)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = true))

            // Next button
            Button(
                onClick = {
                    viewModel.saveAndProceed(onComplete = {
                        onNavigateToTeamPairings(gameId)
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GSAPurple
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "NEXT",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (viewModel.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GSAPurple)
        }
    }
}

@Composable
fun PlayerParticipationCard(
    player: Player,
    onToggleChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Participate in Individual Game",
                    style = MaterialTheme.typography.bodyMedium
                )

                Switch(
                    checked = player.participateInIndividualGame,
                    onCheckedChange = onToggleChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GSAPurple,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}