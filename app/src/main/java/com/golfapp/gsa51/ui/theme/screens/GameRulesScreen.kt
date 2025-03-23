package com.golfapp.gsa51.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.golfapp.gsa51.ui.theme.GSAPurple
import com.golfapp.gsa51.ui.theme.components.GSATopAppBar

@Composable
fun GameRulesScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            GSATopAppBar(
                title = "Game Rules",
                showBackButton = true,
                onBackClick = onNavigateBack,
                onInfoClick = {} // No-op since we're already in the rules screen
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
            // Overview section
            ExpandableRulesSection(
                title = "Overview",
                initiallyExpanded = true,
                content = {
                    Text(
                        "The golf scoring app is designed to track scores and calculate winnings for a 4-player golf game with both individual and team components. Each player plays on their own and also with a partner, with partnerships changing every 6 holes."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The game consists of 18 holes, with scores and winnings calculated according to specific rules for both individual performance and team play."
                    )
                }
            )

            // Individual Game Rules section
            ExpandableRulesSection(
                title = "Individual Game Rules",
                initiallyExpanded = false,
                content = {
                    Text(
                        "For each hole, the player's individual score is compared against all other players:"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Individual game rules as bullet points
                    Text("• If one player has the lowest score, they win the hole in the individual game.")
                    Text("• The winner receives 3 bet units (1 from each of the other players).")
                    Text("• Each losing player gives up 1 bet unit to the winner.")
                    Text("• If there is a tie for lowest score, the hole is declared a draw with no exchange of bet units for that hole.")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Example: On hole 5, Player 1 scores 4, Player 2 scores 5, Player 3 scores 5, and Player 4 scores 6. Player 1 has the lowest score and wins 3 bet units (1 from each other player). Players 2, 3, and 4 each lose 1 bet unit."
                    )
                }
            )

            // Team Game Rules section
            ExpandableRulesSection(
                title = "Team Game Rules",
                initiallyExpanded = false,
                content = {
                    Text(
                        "The team pairings change every 6 holes as follows:"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Holes 1-6: Team 1 (P1 & P2) vs Team 2 (P3 & P4)")
                    Text("• Holes 7-12: Team 1 (P1 & P3) vs Team 2 (P2 & P4)")
                    Text("• Holes 13-18: Team 1 (P1 & P4) vs Team 2 (P2 & P3)")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Team scoring is determined as follows:"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1. Compare the lowest individual score from each team.")
                    Text("2. If one team has a lower score, that team wins the hole.")
                    Text("3. If the lowest scores are tied, compare the next best scores from each team.")
                    Text("4. If the next best scores are also tied, the hole is a draw.")
                    Text("5. When a team wins, each player on the winning team receives 1 bet unit, and each player on the losing team loses 1 bet unit.")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Example: On hole 3, Team 1 (P1 score: 4, P2 score: 5) vs Team 2 (P3 score: 5, P4 score: 4). The lowest scores are P1 and P4 (both 4), so we compare the next scores: P2 (5) vs P3 (5). Since these are also tied, the hole is a draw."
                    )
                }
            )

            // Scoring Calculations section
            ExpandableRulesSection(
                title = "Scoring Calculations",
                initiallyExpanded = false,
                content = {
                    Text(
                        "The final results are calculated as follows:"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Individual Game Total = Individual Winnings - Individual Losses")
                    Text("• Team Game Total = Team Winnings - Team Losses")
                    Text("• Combined Total = Individual Game Total + Team Game Total")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All calculations are based on the bet unit value set at the beginning of the game. For example, if the bet unit is $5:"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Winning an individual hole = 3 × $5 = $15 (winner receives $5 from each of the other three players)")
                    Text("• Losing an individual hole = $5 (loser pays $5 to the winner of the hole)")
                    Text("• Winning a team hole = 1 × $5 = $5 for each player on the winning team")
                    Text("• Losing a team hole = 1 × $5 = $5 loss for each player on the losing team")
                }
            )

            // Back button
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GSAPurple)
            ) {
                Text("BACK TO GAME")
            }
        }
    }
}

@Composable
fun ExpandableRulesSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}