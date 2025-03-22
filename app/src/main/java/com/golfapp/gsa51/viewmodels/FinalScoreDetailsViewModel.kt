package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.golfapp.gsa51.ui.theme.GSAPurple

class FinalScoreDetailsViewModel(
    private val repository: GolfRepository,
    private var gameId: Long = 0L
) : ViewModel() {

    // UI State
    var isLoading by mutableStateOf(true)
        private set

    var game by mutableStateOf<Game?>(null)
        private set

    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    var scoresByHole by mutableStateOf<Map<Int, Map<Long, Score>>>(emptyMap())
        private set

    var totalScores by mutableStateOf<Map<Long, Int>>(emptyMap())
        private set

    // Initialize with game ID
    fun initialize(gameId: Long) {
        this.gameId = gameId
        loadGameData()
    }
    // Get a composable that renders just the scores table for capturing screenshots
    @Composable
    fun ScoresTableForCapture() {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GSAPurple)
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
                val playerCellWidth = 0.7f / players.size
                players.forEach { player ->
                    Box(
                        modifier = Modifier
                            .weight(playerCellWidth)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Table Rows
            for (holeNumber in 1..18) {
                val holeScores = scoresByHole[holeNumber] ?: emptyMap()
                val par = holeScores.values.firstOrNull()?.par ?: 4

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (holeNumber % 2 == 0) Color(0xFFF5F5F5) else Color.White)
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
                            text = "Hole $holeNumber",
                            color = Color(0xFF616161),
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
                            text = par.toString(),
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Player scores
                    val playerCellWidth = 0.7f / players.size
                    players.forEach { player ->
                        val score = holeScores[player.id]
                        if (score != null) {
                            val colorType = getScoreColorType(score.score, score.par)
                            val backgroundColor = when (colorType) {
                                ScoreColorType.EAGLE_PLUS -> Color(0xFF388E3C)
                                ScoreColorType.BIRDIE -> Color(0xFF7CB342)
                                ScoreColorType.PAR -> Color(0xFF2196F3)
                                ScoreColorType.BOGEY -> Color(0xFF444336)
                                ScoreColorType.DOUBLE_PLUS -> Color(0xFFD32F2F)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(playerCellWidth)
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
                                    .weight(playerCellWidth)
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

            // Total Row
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

                // Par total placeholder
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
                val playerCellWidth = 0.7f / players.size
                players.forEach { player ->
                    val totalScore = totalScores[player.id] ?: 0
                    Box(
                        modifier = Modifier
                            .weight(playerCellWidth)
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
    // Load game data from repository
    private fun loadGameData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Load game details
                val gameWithDetails = repository.getGameWithDetails(gameId).first()
                game = gameWithDetails.game

                // Load all scores for this game
                val scores = repository.getAllScoresForGame(gameId).first()

                // Get all player IDs from scores
                val playerIds = scores.map { it.playerId }.distinct()

                // Load player details
                val playersList = mutableListOf<Player>()
                for (playerId in playerIds) {
                    val player = repository.getPlayerById(playerId).first()
                    playersList.add(player)
                }
                players = playersList

                // Group scores by hole and player
                val scoreMap = mutableMapOf<Int, MutableMap<Long, Score>>()
                for (i in 1..18) {
                    scoreMap[i] = mutableMapOf()
                }

                for (score in scores) {
                    val holeScores = scoreMap.getOrPut(score.holeNumber) { mutableMapOf() }
                    holeScores[score.playerId] = score
                }
                scoresByHole = scoreMap

                // Calculate total scores for each player
                totalScores = players.associate { player ->
                    player.id to scores.filter { it.playerId == player.id }.sumOf { it.score }
                }

            } catch (e: Exception) {
                // Handle error
                println("Error loading game data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Determine color for a score based on relation to par
    fun getScoreColorType(score: Int, par: Int): ScoreColorType {
        return when {
            score <= par - 2 -> ScoreColorType.EAGLE_PLUS
            score == par - 1 -> ScoreColorType.BIRDIE
            score == par -> ScoreColorType.PAR
            score == par + 1 -> ScoreColorType.BOGEY
            else -> ScoreColorType.DOUBLE_PLUS
        }
    }
    // Generate a sharable text report of scores
    fun generateScoreReport(): String {
        val report = StringBuilder()

        // Game details
        game?.let { game ->
            report.append("Golf Score Report - ${game.location}\n")
            report.append("Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(game.date)}\n\n")
        }

        // Player names header
        report.append("Hole\tPar\t")
        players.forEach { player ->
            report.append("${player.name}\t")
        }
        report.append("\n")

        // Scores for each hole
        for (hole in 1..18) {
            val holeScores = scoresByHole[hole] ?: continue
            val par = holeScores.values.firstOrNull()?.par ?: 4

            report.append("$hole\t$par\t")

            players.forEach { player ->
                val score = holeScores[player.id]?.score ?: "-"
                report.append("$score\t")
            }
            report.append("\n")
        }

        // Total scores
        report.append("\nTotal\t-\t")
        players.forEach { player ->
            val total = totalScores[player.id] ?: 0
            report.append("$total\t")
        }

        return report.toString()
    }
    // Enum for color types
    enum class ScoreColorType {
        EAGLE_PLUS,
        BIRDIE,
        PAR,
        BOGEY,
        DOUBLE_PLUS
    }
}