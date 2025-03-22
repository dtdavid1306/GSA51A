package com.golfapp.gsa51.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golfapp.gsa51.data.entities.TeamPairing
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamPairingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamPairing(teamPairing: TeamPairing)

    @Update
    suspend fun updateTeamPairing(teamPairing: TeamPairing)

    @Query("SELECT * FROM team_pairings WHERE gameId = :gameId AND section = :section")
    fun getTeamPairingForSection(gameId: Long, section: Int): Flow<TeamPairing?>

    @Query("SELECT * FROM team_pairings WHERE gameId = :gameId")
    fun getAllTeamPairingsForGame(gameId: Long): Flow<List<TeamPairing>>

    @Query("DELETE FROM team_pairings WHERE gameId = :gameId")
    suspend fun deleteAllTeamPairingsForGame(gameId: Long)
}