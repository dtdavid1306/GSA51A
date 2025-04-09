package com.golfapp.gsa51.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.golfapp.gsa51.repositories.GolfRepository
import java.util.Date
import com.golfapp.gsa51.data.entities.Player  // Add this import

class GameDetailsViewModel(private val repository: GolfRepository) : ViewModel() {
    // State for player names
    var player1Name by mutableStateOf("")
        private set
    var player2Name by mutableStateOf("")
        private set
    var player3Name by mutableStateOf("")
        private set
    var player4Name by mutableStateOf("")
        private set

    // State for game details
    var location by mutableStateOf("")
        private set
    var gameDate by mutableStateOf(Date())
        private set
    var betUnit by mutableStateOf(0)  // Changed to 0 for empty field
        private set
    var startingHole by mutableStateOf(0)  // Changed to 0 for empty field
        private set

    // Loading state
    var isLoading by mutableStateOf(false)
        private set
    var maxScoreLimit by mutableStateOf(10)
        private set

    // Update functions for each field
    fun updatePlayer1Name(name: String) {
        player1Name = name
    }

    fun updatePlayer2Name(name: String) {
        player2Name = name
    }

    fun updatePlayer3Name(name: String) {
        player3Name = name
    }

    fun updatePlayer4Name(name: String) {
        player4Name = name
    }

    fun updateLocation(newLocation: String) {
        location = newLocation
    }

    fun updateGameDate(newDate: Date) {
        gameDate = newDate
    }

    fun updateBetUnit(newBetUnit: Int) {
        betUnit = newBetUnit
    }

    fun updateStartingHole(hole: Int) {
        // Default to 1 if saving a zero value
        startingHole = if (hole == 0) 0 else hole

        // Add this function to update the max score limit
        fun updateMaxScoreLimit(limit: Int) {
            maxScoreLimit = limit
        }
    }

    // Update the createGame function in GameDetailsViewModel to return the game ID
    suspend fun createGame(): Long {
        isLoading = true
        // Use default values if fields are empty
        val finalBetUnit = if (betUnit == 0) 1 else betUnit
        val finalStartingHole = if (startingHole == 0) 1 else startingHole

        val gameId = repository.createNewGame(location, gameDate, finalBetUnit, finalStartingHole)
            location
            gameDate
            finalBetUnit
            finalStartingHole
            maxScoreLimit // Add the max score limit

        // Now create player records for this game
        val players = listOf(
            Player(name = player1Name, participateInIndividualGame = true),
            Player(name = player2Name, participateInIndividualGame = true),
            Player(name = player3Name, participateInIndividualGame = true),
            Player(name = player4Name, participateInIndividualGame = true)
        ).filter { it.name.isNotBlank() }

        repository.insertPlayers(players)

        isLoading = false
        return gameId
    }
}