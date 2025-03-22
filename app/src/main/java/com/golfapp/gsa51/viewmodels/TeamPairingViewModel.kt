package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.TeamPairing
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TeamPairingViewModel(
    private val repository: GolfRepository,
    private var gameId: Long = 0L
) : ViewModel() {

    // UI State
    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Track section states
    var section1Completed by mutableStateOf(false)
        private set

    var section2Completed by mutableStateOf(false)
        private set

    var section3Completed by mutableStateOf(false)
        private set

    // Data class to represent a team pairing option
    data class PairingOption(
        val id: Int,
        val description: String,
        val team1Player1Id: Long,
        val team1Player2Id: Long,
        val team2Player1Id: Long,
        val team2Player2Id: Long
    )

    // Selected pairing for each section
    var selectedPairingSection1 by mutableStateOf<PairingOption?>(null)
        private set

    var selectedPairingSection2 by mutableStateOf<PairingOption?>(null)
        private set

    var selectedPairingSection3 by mutableStateOf<PairingOption?>(null)
        private set

    // Available pairing options
    var pairingOptions by mutableStateOf<List<PairingOption>>(emptyList())
        private set

    // Track if all selections are complete
    val allSelectionsComplete: Boolean
        get() = section1Completed && section2Completed && section3Completed

    // Initialize with game ID
    fun initialize(gameId: Long) {
        this.gameId = gameId
        loadPlayers()
        loadExistingPairings()
        generatePairingOptions()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            isLoading = true
            try {
                // First get the game details to get scores
                val gameWithDetails = repository.getGameWithDetails(gameId).first()

                // Extract player IDs from the scores
                val playerIds = gameWithDetails.scores.map { it.playerId }.distinct()

                // If no scores yet (new game), we need to look up players another way
                if (playerIds.isEmpty()) {
                    // Try to get players by gameId from GameDetailsViewModel stored players
                    // Or just load the most recently created players (up to 4)
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
                    players = gamePlayers.filter { it.name.isNotBlank() }.take(4)
                }

                // Generate pairing options after players are loaded
                if (players.size == 4) {
                    generatePairingOptions()
                }

            } catch (e: Exception) {
                // Handle error
                println("Error loading players: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Load any existing team pairings
    private fun loadExistingPairings() {
        viewModelScope.launch {
            try {
                val pairings = repository.getAllTeamPairingsForGame(gameId).first()
                // Update section completion states based on existing pairings
                section1Completed = pairings.any { it.section == 1 }
                section2Completed = pairings.any { it.section == 2 }
                section3Completed = pairings.any { it.section == 3 }
            } catch (e: Exception) {
                println("Error loading team pairings: ${e.message}")
            }
        }
    }
    // Generate all possible pairing options based on 4 players
    private fun generatePairingOptions() {
        // Ensure we have exactly 4 players
        if (players.size != 4) return

        val p1 = players[0]
        val p2 = players[1]
        val p3 = players[2]
        val p4 = players[3]

        pairingOptions = listOf(
            PairingOption(
                id = 1,
                description = "${p1.name} & ${p2.name} vs ${p3.name} & ${p4.name}",
                team1Player1Id = p1.id,
                team1Player2Id = p2.id,
                team2Player1Id = p3.id,
                team2Player2Id = p4.id
            ),
            PairingOption(
                id = 2,
                description = "${p1.name} & ${p3.name} vs ${p2.name} & ${p4.name}",
                team1Player1Id = p1.id,
                team1Player2Id = p3.id,
                team2Player1Id = p2.id,
                team2Player2Id = p4.id
            ),
            PairingOption(
                id = 3,
                description = "${p1.name} & ${p4.name} vs ${p2.name} & ${p3.name}",
                team1Player1Id = p1.id,
                team1Player2Id = p4.id,
                team2Player1Id = p2.id,
                team2Player2Id = p3.id
            )
        )
    }

    // Select a pairing for section 1
    fun selectPairingForSection1(option: PairingOption) {
        selectedPairingSection1 = option
    }

    // Select a pairing for section 2
    fun selectPairingForSection2(option: PairingOption) {
        // Ensure different from section 1
        if (selectedPairingSection1?.id == option.id) {
            // Handle invalid selection
            return
        }
        selectedPairingSection2 = option
    }

    // Auto-fill section 3 based on previous selections
    private fun autoFillSection3() {
        // Find the option that hasn't been used yet
        val usedIds = setOf(selectedPairingSection1?.id, selectedPairingSection2?.id)
        selectedPairingSection3 = pairingOptions.firstOrNull { it.id !in usedIds }
    }

    // Confirm pairing for section 1
    fun confirmSection1() {
        if (selectedPairingSection1 == null) return

        viewModelScope.launch {
            try {
                // Create and save the team pairing entity
                val teamPairing = TeamPairing(
                    gameId = gameId,
                    section = 1,
                    team1Player1Id = selectedPairingSection1!!.team1Player1Id,
                    team1Player2Id = selectedPairingSection1!!.team1Player2Id,
                    team2Player1Id = selectedPairingSection1!!.team2Player1Id,
                    team2Player2Id = selectedPairingSection1!!.team2Player2Id
                )
                repository.insertTeamPairing(teamPairing)

                // Update state
                section1Completed = true
            } catch (e: Exception) {
                // Handle error
                println("Error saving team pairing: ${e.message}")
            }
        }
    }

    // Confirm pairing for section 2
    fun confirmSection2() {
        if (selectedPairingSection2 == null) return

        viewModelScope.launch {
            try {
                // Create and save the team pairing entity
                val teamPairing = TeamPairing(
                    gameId = gameId,
                    section = 2,
                    team1Player1Id = selectedPairingSection2!!.team1Player1Id,
                    team1Player2Id = selectedPairingSection2!!.team1Player2Id,
                    team2Player1Id = selectedPairingSection2!!.team2Player1Id,
                    team2Player2Id = selectedPairingSection2!!.team2Player2Id
                )
                repository.insertTeamPairing(teamPairing)

                // Update state
                section2Completed = true

                // Auto-fill section 3
                autoFillSection3()

                // Save section 3 automatically
                confirmSection3()
            } catch (e: Exception) {
                // Handle error
                println("Error saving team pairing: ${e.message}")
            }
        }
    }

    // Confirm pairing for section 3 (auto-filled)
    private fun confirmSection3() {
        if (selectedPairingSection3 == null) return

        viewModelScope.launch {
            try {
                // Create and save the team pairing entity
                val teamPairing = TeamPairing(
                    gameId = gameId,
                    section = 3,
                    team1Player1Id = selectedPairingSection3!!.team1Player1Id,
                    team1Player2Id = selectedPairingSection3!!.team1Player2Id,
                    team2Player1Id = selectedPairingSection3!!.team2Player1Id,
                    team2Player2Id = selectedPairingSection3!!.team2Player2Id
                )
                repository.insertTeamPairing(teamPairing)

                // Update state
                section3Completed = true
            } catch (e: Exception) {
                // Handle error
                println("Error saving team pairing: ${e.message}")
            }
        }
    }

    // Reset all selections
    fun resetSelections() {
        viewModelScope.launch {
            try {
                // Delete all team pairings for this game
                repository.deleteAllTeamPairingsForGame(gameId)

                // Reset state
                selectedPairingSection1 = null
                selectedPairingSection2 = null
                selectedPairingSection3 = null
                section1Completed = false
                section2Completed = false
                section3Completed = false
            } catch (e: Exception) {
                // Handle error
                println("Error resetting selections: ${e.message}")
            }
        }
    }
}
