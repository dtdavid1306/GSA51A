package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IndividualGameSettingsViewModel(
    private val repository: GolfRepository,
    private var gameId: Long = 0L
) : ViewModel() {

    // UI State
    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun initialize(gameId: Long) {
        this.gameId = gameId
        loadPlayers()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            isLoading = true

            try {
                // Get the game with its relationships
                val gameWithDetails = repository.getGameWithDetails(gameId).first()

                // Extract player IDs from the scores if any
                val playerIds = gameWithDetails.scores.map { it.playerId }.distinct()

                if (playerIds.isEmpty()) {
                    // If no scores yet, get the most recently created players (up to 4)
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

                // Log the number of players for debugging
                println("Loaded ${players.size} players for game $gameId")
            } catch (e: Exception) {
                // Handle any errors
                println("Error loading players: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun updatePlayerParticipation(playerId: Long, participates: Boolean) {
        viewModelScope.launch {
            val playerToUpdate = players.find { it.id == playerId } ?: return@launch
            val updatedPlayer = playerToUpdate.copy(participateInIndividualGame = participates)
            repository.updatePlayer(updatedPlayer)

            // Update local state
            players = players.map {
                if (it.id == playerId) updatedPlayer else it
            }
        }
    }

    fun saveAndProceed(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Make sure all changes are saved
            players.forEach { player ->
                repository.updatePlayer(player)
            }
            // Navigate to next screen
            onComplete()
        }
    }
}
