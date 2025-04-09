package com.golfapp.gsa51.repositories

import com.golfapp.gsa51.data.GSA51Database
import com.golfapp.gsa51.data.dao.GameWithDetails
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.data.entities.TeamPairing
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GolfRepository(private val database: GSA51Database) {

    // Player operations
    fun getAllPlayers(): Flow<List<Player>> = database.playerDao().getAllPlayers()

    fun getPlayerById(playerId: Long): Flow<Player> = database.playerDao().getPlayerById(playerId)

    suspend fun insertPlayer(player: Player): Long = database.playerDao().insertPlayer(player)

    suspend fun insertPlayers(players: List<Player>): List<Long> = database.playerDao().insertPlayers(players)

    suspend fun updatePlayer(player: Player) = database.playerDao().updatePlayer(player)

    suspend fun deletePlayer(player: Player) = database.playerDao().deletePlayer(player)

    // Game operations
    fun getAllGames(): Flow<List<Game>> = database.gameDao().getAllGames()

    fun getActiveGames(): Flow<List<Game>> = database.gameDao().getActiveGames()

    fun getCompletedGames(): Flow<List<Game>> = database.gameDao().getCompletedGames()

    fun getGameById(gameId: Long): Flow<Game> = database.gameDao().getGameById(gameId)

    // Update this function to include maxScoreLimit
    suspend fun createNewGame(
        location: String,
        date: Date,
        betUnit: Int,
        startingHole: Int = 1,
        maxScoreLimit: Int = 10 // Add this parameter with default value 10
    ): Long {
        return database.gameDao().insertGame(
            Game(
                location = location,
                date = date,
                betUnit = betUnit,
                startingHole = startingHole,
                currentHole = startingHole, // Initialize currentHole to startingHole
                isCompleted = false,
                maxScoreLimit = maxScoreLimit // Add this field
            )
        )
    }

    suspend fun updateGame(game: Game) = database.gameDao().updateGame(game)

    suspend fun deleteGame(game: Game) = database.gameDao().deleteGame(game)

    // Score operations
    fun getScoresForHole(gameId: Long, holeNumber: Int): Flow<List<Score>> =
        database.scoreDao().getScoresForHole(gameId, holeNumber)

    fun getPlayerScoresForGame(gameId: Long, playerId: Long): Flow<List<Score>> =
        database.scoreDao().getPlayerScoresForGame(gameId, playerId)

    fun getAllScoresForGame(gameId: Long): Flow<List<Score>> =
        database.scoreDao().getAllScoresForGame(gameId)

    suspend fun insertScore(score: Score) = database.scoreDao().insertScore(score)

    suspend fun insertScores(scores: List<Score>) = database.scoreDao().insertScores(scores)

    suspend fun updateScore(score: Score) = database.scoreDao().updateScore(score)

    suspend fun deleteAllScoresForGame(gameId: Long) =
        database.scoreDao().deleteAllScoresForGame(gameId)

    // Team Pairing operations
    fun getTeamPairingForSection(gameId: Long, section: Int): Flow<TeamPairing?> =
        database.teamPairingDao().getTeamPairingForSection(gameId, section)

    fun getAllTeamPairingsForGame(gameId: Long): Flow<List<TeamPairing>> =
        database.teamPairingDao().getAllTeamPairingsForGame(gameId)

    suspend fun insertTeamPairing(teamPairing: TeamPairing) =
        database.teamPairingDao().insertTeamPairing(teamPairing)

    suspend fun updateTeamPairing(teamPairing: TeamPairing) =
        database.teamPairingDao().updateTeamPairing(teamPairing)

    suspend fun deleteAllTeamPairingsForGame(gameId: Long) =
        database.teamPairingDao().deleteAllTeamPairingsForGame(gameId)

    // Game with relations
    fun getGameWithDetails(gameId: Long): Flow<GameWithDetails> =
        database.gameWithRelationsDao().getGameWithRelations(gameId)

}