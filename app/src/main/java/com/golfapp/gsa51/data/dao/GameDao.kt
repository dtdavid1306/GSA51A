package com.golfapp.gsa51.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golfapp.gsa51.data.entities.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameById(gameId: Long): Flow<Game>

    @Query("SELECT * FROM games ORDER BY date DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE isCompleted = 0 ORDER BY date DESC")
    fun getActiveGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE isCompleted = 1 ORDER BY date DESC")
    fun getCompletedGames(): Flow<List<Game>>
}