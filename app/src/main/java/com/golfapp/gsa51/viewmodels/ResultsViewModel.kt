package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.data.entities.TeamPairing
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


class ResultsViewModel(
    private val repository: GolfRepository,
    private var gameId: Long = 0L
) : ViewModel() {
    // UI State
    var game by mutableStateOf<Game?>(null)
        private set

    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    var scores by mutableStateOf<List<Score>>(emptyList())
        private set

    var teamPairings by mutableStateOf<List<TeamPairing>>(emptyList())
        private set

    var playerResults by mutableStateOf<Map<Long, PlayerResult>>(emptyMap())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Private backing field with direct assignment
    private val _isSharing = mutableStateOf(false)
    // Public read-only property
    val isSharing: Boolean get() = _isSharing.value

    // Method to update state
    fun setSharing(sharing: Boolean) {
        _isSharing.value = sharing
    }

    // Render scores table for screenshot
    @Composable
    fun ScoresTableForCapture() {
        Column(modifier = Modifier.background(Color.White)) {
            // Add game info at the top
            game?.let { game ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Final Score Details",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Legend:", style = MaterialTheme.typography.bodyMedium)
                        Surface(color = Color(0xFF388E3C), shape = MaterialTheme.shapes.small) {
                            Text("Eagle+", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                        Surface(color = Color(0xFF7CB342), shape = MaterialTheme.shapes.small) {
                            Text("Birdie", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                        Surface(color = Color(0xFF2196F3), shape = MaterialTheme.shapes.small) {
                            Text("Par", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                        Surface(color = Color(0xFFF44336), shape = MaterialTheme.shapes.small) {
                            Text("Bogey", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                        Surface(color = Color(0xFFD32F2F), shape = MaterialTheme.shapes.small) {
                            Text("Double+", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6200EE))
                    .padding(4.dp)
            ) {
                // Hole column
                Box(
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hole",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Par column
                Box(
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Par",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Player name columns
                val playerNames = playerResults.values.map { it.player.name }
                val playerWidth = 0.7f / playerNames.size
                playerNames.forEach { name ->
                    Box(
                        modifier = Modifier
                            .weight(playerWidth)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Table rows
            val allHoles = scores.map { it.holeNumber }.distinct().sorted()
            for (hole in allHoles) {
                val holeScores = scores.filter { it.holeNumber == hole }
                val par = holeScores.firstOrNull()?.par ?: 4 // Default to par 4

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (hole % 2 == 0) Color(0xFFF5F5F5) else Color.White)
                        .padding(4.dp)
                ) {
                    // Hole number
                    Box(
                        modifier = Modifier
                            .weight(0.15f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hole $hole",
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Par value
                    Box(
                        modifier = Modifier
                            .weight(0.15f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = par.toString(),
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Player scores
                    val playerIds = playerResults.keys.toList()
                    val playerWidth = 0.7f / playerIds.size
                    playerIds.forEach { playerId ->
                        val score = holeScores.find { it.playerId == playerId }
                        if (score != null) {
                            val backgroundColor = when {
                                score.score <= score.par - 2 -> Color(0xFF388E3C) // Eagle+
                                score.score == score.par - 1 -> Color(0xFF7CB342) // Birdie
                                score.score == score.par -> Color(0xFF2196F3) // Par
                                score.score == score.par + 1 -> Color(0xFFF44336) // Bogey
                                else -> Color(0xFFD32F2F) // Double+
                            }

                            Box(
                                modifier = Modifier
                                    .weight(playerWidth)
                                    .background(backgroundColor)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = score.score.toString(),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(playerWidth)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "-",
                                    color = Color(0xFF616161),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Total row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(4.dp)
            ) {
                // Total label
                Box(
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Total",
                        color = Color(0xFF212121),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Par placeholder
                Box(
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        color = Color(0xFF212121),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Player totals
                val playerIds = playerResults.keys.toList()
                val playerWidth = 0.7f / playerIds.size
                playerIds.forEach { playerId ->
                    val totalScore = playerResults[playerId]?.totalScore ?: 0
                    Box(
                        modifier = Modifier
                            .weight(playerWidth)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = totalScore.toString(),
                            color = Color(0xFF212121),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    // Initialize with game ID
    fun initialize(gameId: Long) {
        this.gameId = gameId
        loadGameData()
    }

    // Load game data from repository
    private fun loadGameData() {
        viewModelScope.launch {
            isLoading = true

            try {
                // Load game data from repository
                val gameWithDetails = repository.getGameWithDetails(gameId).first()
                game = gameWithDetails.game
                scores = gameWithDetails.scores
                teamPairings = gameWithDetails.teamPairings

                // Load players
                val playerIds = scores.map { it.playerId }.distinct()
                val loadedPlayers = mutableListOf<Player>()
                for (playerId in playerIds) {
                    loadedPlayers.add(repository.getPlayerById(playerId).first())
                }
                players = loadedPlayers

                // Calculate results
                calculateResults()

            } catch (e: Exception) {
                // Handle error
                println("Error loading game data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Call this after loading player data
    private fun calculateResults() {
        val results = mutableMapOf<Long, PlayerResult>()

        // Process each player
        for (player in players) {
            val individualResults = calculateIndividualResults(player)
            val teamResults = calculateTeamResults(player)

            // Calculate total score for player
            val totalScore = scores
                .filter { it.playerId == player.id }
                .sumOf { it.score }

            results[player.id] = PlayerResult(
                player = player,
                totalScore = totalScore,
                individualWins = individualResults.wins,
                individualDraws = individualResults.draws,
                individualLosses = individualResults.losses,
                individualWinnings = individualResults.winnings,
                teamWins = teamResults.wins,
                teamDraws = teamResults.draws,
                teamLosses = teamResults.losses,
                teamWinnings = teamResults.winnings,
                combinedWinnings = individualResults.winnings + teamResults.winnings
            )
        }

        playerResults = results
    }

    // Calculate individual game results for a player
    private fun calculateIndividualResults(player: Player): GameResult {
        // If player opted out of individual game, return zeros
        if (!player.participateInIndividualGame) {
            return GameResult(0, 0, 0, 0f)
        }

        var wins = 0
        var draws = 0
        var losses = 0
        var winnings = 0f
        val betUnit = game?.betUnit?.toFloat() ?: 1f

        // Get participating players (excluding the current player)
        val participatingPlayers = players.filter {
            it.participateInIndividualGame && it.id != player.id
        }

        // Calculate for each hole (1-18)
        for (hole in 1..18) {
            // Get scores for this hole
            val holeScores = scores.filter { it.holeNumber == hole }

            // Get scores for participating players
            val participatingScores = holeScores.filter { score ->
                players.any { it.id == score.playerId && it.participateInIndividualGame }
            }

            if (participatingScores.isEmpty()) continue

            // Find current player's score
            val playerScore = participatingScores.find { it.playerId == player.id }
                ?: continue

            // Find minimum score for the hole
            val minScore = participatingScores.minOf { it.score }

            // Count players with minimum score
            val playersWithMinScore = participatingScores.count { it.score == minScore }

            when {
                // Player has lowest score and is the only one
                playerScore.score == minScore && playersWithMinScore == 1 -> {
                    wins++
                    // Win bet unit from each participating player
                    winnings += betUnit * participatingPlayers.size
                }
                // Player has lowest score but tied with others
                playerScore.score == minScore && playersWithMinScore > 1 -> {
                    draws++
                    // Draw - no winnings
                }
                // Player doesn't have lowest score
                else -> {
                    // Only count as a loss if there's a clear winner (no draw)
                    if (playersWithMinScore == 1) {
                        losses++
                        // Lose bet unit to the winner
                        winnings -= betUnit
                    } else {
                        draws++
                        // Draw - no winnings
                    }
                }
            }
        }

        return GameResult(wins, draws, losses, winnings)
    }

    // Calculate team game results for a player
    private fun calculateTeamResults(player: Player): GameResult {
        var wins = 0
        var draws = 0
        var losses = 0
        var winnings = 0f
        val betUnit = game?.betUnit?.toFloat() ?: 1f
        val startingHole = game?.startingHole ?: 1

        // Calculate for each hole (1-18)
        for (hole in 1..18) {
            // Calculate which section this hole belongs to based on starting hole
            val adjustedPosition = if (hole >= startingHole) {
                hole - startingHole + 1
            } else {
                hole + (19 - startingHole)
            }

            // Determine section based on position
            val section = (adjustedPosition - 1) / 6 + 1

            // Find team pairing for this section
            val pairing = teamPairings.find { it.section == section } ?: continue

            // Determine which team the player is on
            val isTeam1 = pairing.team1Player1Id == player.id || pairing.team1Player2Id == player.id
            val isTeam2 = pairing.team2Player1Id == player.id || pairing.team2Player2Id == player.id

            if (!isTeam1 && !isTeam2) continue

            // Get all scores for this hole
            val holeScores = scores.filter { it.holeNumber == hole }
            if (holeScores.size < 4) continue

            // Get team members
            val team1Players = listOf(pairing.team1Player1Id, pairing.team1Player2Id)
            val team2Players = listOf(pairing.team2Player1Id, pairing.team2Player2Id)

            // Get scores for each team
            val team1Scores = holeScores
                .filter { it.playerId in team1Players }
                .map { it.score }
                .sorted()

            val team2Scores = holeScores
                .filter { it.playerId in team2Players }
                .map { it.score }
                .sorted()

            if (team1Scores.size < 2 || team2Scores.size < 2) continue

            // Compare lowest scores
            val team1BestScore = team1Scores[0]
            val team2BestScore = team2Scores[0]

            when {
                // Team 1 wins with best player
                team1BestScore < team2BestScore -> {
                    if (isTeam1) {
                        wins++
                        // Win exactly one bet unit total
                        winnings += betUnit
                    } else { // isTeam2
                        losses++
                        // Lose exactly one bet unit total
                        winnings -= betUnit
                    }
                }
                // Team 2 wins with best player
                team2BestScore < team1BestScore -> {
                    if (isTeam2) {
                        wins++
                        // Win exactly one bet unit total
                        winnings += betUnit
                    } else { // isTeam1
                        losses++
                        // Lose exactly one bet unit total
                        winnings -= betUnit
                    }
                }
                // Best player tied, compare second best
                else -> {
                    val team1SecondScore = team1Scores[1]
                    val team2SecondScore = team2Scores[1]

                    when {
                        // Team 1 wins with second player
                        team1SecondScore < team2SecondScore -> {
                            if (isTeam1) {
                                wins++
                                // Win exactly one bet unit total
                                winnings += betUnit
                            } else { // isTeam2
                                losses++
                                // Lose exactly one bet unit total
                                winnings -= betUnit
                            }
                        }
                        // Team 2 wins with second player
                        team2SecondScore < team1SecondScore -> {
                            if (isTeam2) {
                                wins++
                                // Win exactly one bet unit total
                                winnings += betUnit
                            } else { // isTeam1
                                losses++
                                // Lose exactly one bet unit total
                                winnings -= betUnit
                            }
                        }
                        // Both players tied - draw
                        else -> {
                            draws++
                            // No winnings changes on draw
                        }
                    }
                }
            }
        }

        return GameResult(wins, draws, losses, winnings)
    }
    // Generate a sharable report of game results
    fun generateShareableReport(): String {
        val report = StringBuilder()

        // Game details
        game?.let { game ->
            report.append("GOLF SCORE REPORT\n")
            report.append("----------------\n")
            report.append("Location: ${game.location}\n")
            report.append("Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(game.date)}\n")
            report.append("Bet Unit: $${game.betUnit}\n\n")
        }

        // PART 1: SUMMARY
        report.append("RESULTS SUMMARY\n")
        report.append("----------------\n\n")

        // Individual results
        report.append("INDIVIDUAL GAME:\n")
        playerResults.values.forEach { result ->
            val playerName = result.player.name
            if (result.player.participateInIndividualGame) {
                report.append("$playerName: W${result.individualWins} D${result.individualDraws} L${result.individualLosses} = $${String.format("%.2f", result.individualWinnings)}\n")
            } else {
                report.append("$playerName: Did not participate\n")
            }
        }

        // Team results
        report.append("\nTEAM GAME:\n")
        playerResults.values.forEach { result ->
            val playerName = result.player.name
            report.append("$playerName: W${result.teamWins} D${result.teamDraws} L${result.teamLosses} = $${String.format("%.2f", result.teamWinnings)}\n")
        }

        // Combined totals
        report.append("\nCOMBINED TOTALS:\n")
        val sortedResults = playerResults.values.toList().sortedByDescending { it.combinedWinnings }
        for (result in sortedResults) {
            val playerName = result.player.name
            report.append("$playerName: $${String.format("%.2f", result.combinedWinnings)}\n")
        }

        return report.toString()
    }
    data class PlayerResult(
        val player: Player,
        val totalScore: Int,
        val individualWins: Int,
        val individualDraws: Int,
        val individualLosses: Int,
        val individualWinnings: Float,
        val teamWins: Int,
        val teamDraws: Int,
        val teamLosses: Int,
        val teamWinnings: Float,
        val combinedWinnings: Float
    )

    data class GameResult(
        val wins: Int,
        val draws: Int,
        val losses: Int,
        val winnings: Float
    )

    enum class HoleResult {
        WIN, DRAW, LOSS
    }
}