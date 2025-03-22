package com.golfapp.gsa51.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golfapp.gsa51.data.entities.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<Player>): List<Long>

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT * FROM players WHERE id = :playerId")
    fun getPlayerById(playerId: Long): Flow<Player>

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<Player>>
}