package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedGamesViewModel(
    private val repository: GolfRepository
) : ViewModel() {

    // UI State
    var isLoading by mutableStateOf(true)
        private set

    var games by mutableStateOf<List<SavedGameItem>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var showDeleteConfirmation by mutableStateOf(false)
        private set

    var gameToDelete by mutableStateOf<Game?>(null)
        private set

    // Initialize by loading games
    init {
        loadGames()
    }

    // Load all games
    fun loadGames() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Get all games
                val allGames = repository.getAllGames().first()

                // Create SavedGameItem list with player info
                val gameItems = mutableListOf<SavedGameItem>()

                for (game in allGames) {
                    val gameWithDetails = repository.getGameWithDetails(game.id).first()

                    // Get player IDs from scores
                    val playerIds = gameWithDetails.scores.map { it.playerId }.distinct()

                    // Get player names
                    val playerNames = mutableListOf<String>()
                    for (playerId in playerIds) {
                        val player = repository.getPlayerById(playerId).first()
                        playerNames.add(player.name)
                    }

                    // Create SavedGameItem
                    gameItems.add(
                        SavedGameItem(
                            game = game,
                            playerNames = playerNames.joinToString(", ")
                        )
                    )
                }

                games = gameItems
            } catch (e: Exception) {
                errorMessage = "Error loading games: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Initiate delete confirmation
    fun confirmDelete(game: Game) {
        gameToDelete = game
        showDeleteConfirmation = true
    }

    // Cancel delete
    fun cancelDelete() {
        gameToDelete = null
        showDeleteConfirmation = false
    }

    // Delete a game
    fun deleteGame() {
        viewModelScope.launch {
            try {
                gameToDelete?.let { game ->
                    repository.deleteGame(game)
                    loadGames()
                }
            } catch (e: Exception) {
                errorMessage = "Error deleting game: ${e.message}"
            } finally {
                showDeleteConfirmation = false
                gameToDelete = null
            }
        }
    }

    // Data class for UI representation
    data class SavedGameItem(
        val game: Game,
        val playerNames: String
    )
}