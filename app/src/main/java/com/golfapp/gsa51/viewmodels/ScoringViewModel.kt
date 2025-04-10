package com.golfapp.gsa51.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.PreferencesManager
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.data.entities.TeamPairing
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScoringViewModel(
    private val repository: GolfRepository,
    private val preferencesManager: PreferencesManager,
    private var gameId: Long = 0L
) : ViewModel() {

    // Initialize with default, will be properly set in initialize()
    var maxScoreLimit by mutableStateOf(15)
        private set

    // UI State
    var game by mutableStateOf<Game?>(null)
        private set

    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    var currentHole by mutableStateOf(1)
        private set

    var scores by mutableStateOf<Map<Long, Int>>(emptyMap())
        private set

    var holePar by mutableStateOf<Int?>(null)
        private set

    var teamPairings by mutableStateOf<List<TeamPairing>>(emptyList())
        private set

    var currentSection by mutableStateOf(1)
        private set

    var currentTeamPairing by mutableStateOf<TeamPairing?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var hasUnsavedChanges by mutableStateOf(false)
        private set

    var navigateToHoleInput by mutableStateOf("")
        private set

    var allHolesScored by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Add this property to track par confirmation
    var isParConfirmed by mutableStateOf(false)
        private set

    // Initialize with game ID
    fun initialize(gameId: Long) {
        this.gameId = gameId

        // Always get the max score limit from preferences first
        maxScoreLimit = preferencesManager.getMaxScoreLimit()
        Log.d("ScoringVM", "Loaded limit from preferences: $maxScoreLimit")

        loadGame()
        loadPlayers()
        loadTeamPairings()
        checkAllHolesScored()
    }

    // Update the loadGame function to NOT override the maxScoreLimit
    private fun loadGame() {
        viewModelScope.launch {
            isLoading = true
            try {
                val loadedGame = repository.getGameById(gameId).first()
                game = loadedGame

                // REMOVED: Don't override the max score limit from preferences
                // maxScoreLimit = loadedGame.maxScoreLimit

                // Set current hole to starting hole initially
                game?.let {
                    if (currentHole == 1) { // Only set if not already navigated
                        currentHole = it.startingHole
                    }
                }
                updateCurrentSection()
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Error loading game: ${e.message}"
                println(errorMessage)
            } finally {
                isLoading = false
            }
        }
    }

    // Load players for this game
    private fun loadPlayers() {
        viewModelScope.launch {
            try {
                // Get the game with its relationships
                val gameWithDetails = repository.getGameWithDetails(gameId).first()

                // Extract player IDs from the scores if any
                val playerIds = gameWithDetails.scores.map { it.playerId }.distinct()

                if (playerIds.isEmpty()) {
                    // If no scores yet (new game), we need to look up players another way
                    players = repository.getAllPlayers().first()
                        .filter { it.name.isNotBlank() }
                        .sortedByDescending { it.id }  // Most recent first
                        .take(4)
                } else {
                    // Get players from scores
                    val gamePlayers = mutableListOf<Player>()
                    for (playerId in playerIds) {
                        val player = repository.getPlayerById(playerId).first()
                        gamePlayers.add(player)
                    }
                    players = gamePlayers.filter { it.name.isNotBlank() }
                }

                // Load scores for the current hole
                loadScoresForCurrentHole()
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Error loading players: ${e.message}"
                println(errorMessage)
            }
        }
    }

    // Load team pairings for this game
    private fun loadTeamPairings() {
        viewModelScope.launch {
            try {
                teamPairings = repository.getAllTeamPairingsForGame(gameId).first()
                updateCurrentSection()
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Error loading team pairings: ${e.message}"
                println(errorMessage)
            }
        }
    }

    // Modify loadScoresForCurrentHole to update isParConfirmed
    private fun loadScoresForCurrentHole() {
        viewModelScope.launch {
            try {
                val holeScores = repository.getScoresForHole(gameId, currentHole).first()

                if (holeScores.isEmpty()) {
                    // If no scores exist for this hole, clear the scores and par
                    scores = emptyMap()
                    holePar = null
                    isParConfirmed = false
                } else {
                    // Convert to map of playerId -> score for easier access
                    scores = holeScores.associate { it.playerId to it.score }

                    // Load par value if available
                    holePar = holeScores.first().par
                    isParConfirmed = holePar != null
                }

                hasUnsavedChanges = false
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Error loading scores: ${e.message}"
                println(errorMessage)
            }
        }
    }

    // Check if all 18 holes have scores
    private fun checkAllHolesScored() {
        viewModelScope.launch {
            try {
                val allScores = repository.getAllScoresForGame(gameId).first()
                val holesWithScores = allScores.map { it.holeNumber }.distinct()
                allHolesScored = holesWithScores.size == 18 &&
                        holesWithScores.all { it in 1..18 } &&
                        players.all { player ->
                            holesWithScores.all { hole ->
                                allScores.any { it.playerId == player.id && it.holeNumber == hole }
                            }
                        }
            } catch (e: Exception) {
                errorMessage = "Error checking hole completion: ${e.message}"
                println(errorMessage)
            }
        }
    }



    fun updateScore(playerId: Long, scoreStr: String) {
        Log.d("ScoringVM", "Validating score: $scoreStr against limit: $maxScoreLimit")

        val score = scoreStr.toIntOrNull()
        if (score != null) {
            // Check against the current maxScoreLimit
            if (score > maxScoreLimit) {
                Log.d("ScoringVM", "Score $score exceeds limit $maxScoreLimit")
                // Show error and clear the field
                setError("Score cannot exceed $maxScoreLimit. Please enter a value between 1 and $maxScoreLimit.")

                // Remove the invalid score
                scores = scores.toMutableMap().apply {
                    remove(playerId)  // Remove instead of update
                }
                hasUnsavedChanges = true
                return
            }

            // Valid score - update as normal
            scores = scores.toMutableMap().apply {
                put(playerId, score)
            }
            hasUnsavedChanges = true
        }
    }


    // Update the updatePar method to reset confirmation
    fun updatePar(parStr: String) {
        val par = parStr.toIntOrNull()
        if (par != null && par in 3..5) {
            holePar = par
            hasUnsavedChanges = true
            // Reset par confirmation when par is changed
            isParConfirmed = false
        }
    }

    // Check if par is valid
    fun isParValid(): Boolean {
        return holePar != null && holePar in 3..5
    }

    // Auto-save scores with callback
    fun autoSave(onComplete: () -> Unit = {}) {
        if (!isParValid()) {
            setError("Please enter a par value (3-5)")
            return
        }
        // Add validation for scores
        val invalidScores = scores.filter { (_, score) -> score > maxScoreLimit }
        if (invalidScores.isNotEmpty()) {
            setError("Some scores exceed the maximum limit of $maxScoreLimit. Please correct them before saving.")
            return
        }
        viewModelScope.launch {
            try {
                // Create Score entities from current scores
                val scoreEntities = scores.map { (playerId, score) ->
                    Score(
                        gameId = gameId,
                        playerId = playerId,
                        holeNumber = currentHole,
                        score = score,
                        par = holePar ?: 4 // Fallback to 4 if somehow null
                    )
                }

                // Only save if we have scores for all players
                if (scoreEntities.size == players.size) {
                    // Save to database
                    repository.insertScores(scoreEntities)
                    hasUnsavedChanges = false
                    checkAllHolesScored()
                    onComplete()
                } else {
                    setError("Please enter scores for all players")
                }
            } catch (e: Exception) {
                // Handle error
                setError("Error saving scores: ${e.message}")
                println(errorMessage)
            }
        }
    }

    // Save scores to database
    fun saveScores() {
        autoSave()
    }

    // Navigate to next hole - UPDATED: removed Par validation
    fun nextHole() {
        if (hasUnsavedChanges) {
            autoSave {
                // Allow circular navigation from hole 18 to hole 1
                currentHole = if (currentHole == 18) 1 else currentHole + 1
                loadScoresForCurrentHole()
                updateCurrentSection()
                updateCurrentHoleInGame()
            }
        } else {
            // Allow circular navigation from hole 18 to hole 1
            currentHole = if (currentHole == 18) 1 else currentHole + 1
            loadScoresForCurrentHole()
            updateCurrentSection()
            updateCurrentHoleInGame()
        }
    }

    // Navigate to previous hole - UPDATED: removed Par validation
    fun previousHole() {
        if (hasUnsavedChanges) {
            autoSave {
                // Allow circular navigation from hole 1 to hole 18
                currentHole = if (currentHole == 1) 18 else currentHole - 1
                loadScoresForCurrentHole()
                updateCurrentSection()
                updateCurrentHoleInGame()
            }
        } else {
            // Allow circular navigation from hole 1 to hole 18
            currentHole = if (currentHole == 1) 18 else currentHole - 1
            loadScoresForCurrentHole()
            updateCurrentSection()
            updateCurrentHoleInGame()
        }
    }

    // Navigate to specific hole - UPDATED: removed Par validation
    fun navigateToHole(holeStr: String) {
        val hole = holeStr.toIntOrNull()
        if (hole != null && hole in 1..18) {
            if (hasUnsavedChanges) {
                autoSave {
                    currentHole = hole
                    loadScoresForCurrentHole()
                    updateCurrentSection()
                    navigateToHoleInput = ""
                    updateCurrentHoleInGame()
                }
            } else {
                currentHole = hole
                loadScoresForCurrentHole()
                updateCurrentSection()
                navigateToHoleInput = ""
                updateCurrentHoleInGame()
            }
        } else {
            setError("Please enter a valid hole number (1-18)")
        }
    }

    // Update navigate to hole input
    fun updateNavigateToHoleInput(input: String) {
        navigateToHoleInput = input
    }

    // Add a method to confirm the par
    fun confirmPar(): Boolean {
        if (isParValid()) {
            isParConfirmed = true
            return true
        }
        return false
    }
    // Update current hole in the game entity
    private fun updateCurrentHoleInGame() {
        viewModelScope.launch {
            try {
                game?.let { currentGame ->
                    val updatedGame = currentGame.copy(currentHole = currentHole)
                    repository.updateGame(updatedGame)
                }
            } catch (e: Exception) {
                // Handle error
                println("Error updating current hole: ${e.message}")
            }
        }
    }

    // Update current section based on hole number and starting hole
    private fun updateCurrentSection() {
        // Get starting hole from game (default to 1 if not available)
        val startingHole = game?.startingHole ?: 1

        // Calculate adjusted position (1-18) based on starting hole
        val adjustedPosition = if (currentHole >= startingHole) {
            currentHole - startingHole + 1
        } else {
            currentHole + (19 - startingHole)
        }

        // Determine section based on adjusted position
        currentSection = (adjustedPosition - 1) / 6 + 1

        // Find the team pairing for this section
        currentTeamPairing = teamPairings.find { it.section == currentSection }
    }

    // Get player names for the current team pairing
    fun getTeamMemberNames(teamNumber: Int): Pair<String, String> {
        val pairing = currentTeamPairing ?: return Pair("", "")

        val (player1Id, player2Id) = if (teamNumber == 1) {
            Pair(pairing.team1Player1Id, pairing.team1Player2Id)
        } else {
            Pair(pairing.team2Player1Id, pairing.team2Player2Id)
        }

        val player1 = players.find { it.id == player1Id }
        val player2 = players.find { it.id == player2Id }

        return Pair(player1?.name ?: "", player2?.name ?: "")
    }

    // Clear error message
    fun clearError() {
        errorMessage = null
    }

    // Update setError to add logging
    fun setError(message: String) {
        Log.d("ScoringVM", "Setting error message: $message")
        errorMessage = message
    }
}