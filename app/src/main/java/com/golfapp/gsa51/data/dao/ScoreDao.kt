package com.golfapp.gsa51.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golfapp.gsa51.data.entities.Score
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: Score)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: List<Score>)

    @Update
    suspend fun updateScore(score: Score)

    @Query("SELECT * FROM scores WHERE gameId = :gameId AND holeNumber = :holeNumber")
    fun getScoresForHole(gameId: Long, holeNumber: Int): Flow<List<Score>>

    @Query("SELECT * FROM scores WHERE gameId = :gameId AND playerId = :playerId")
    fun getPlayerScoresForGame(gameId: Long, playerId: Long): Flow<List<Score>>

    @Query("SELECT * FROM scores WHERE gameId = :gameId ORDER BY holeNumber")
    fun getAllScoresForGame(gameId: Long): Flow<List<Score>>

    @Query("DELETE FROM scores WHERE gameId = :gameId")
    suspend fun deleteAllScoresForGame(gameId: Long)
}