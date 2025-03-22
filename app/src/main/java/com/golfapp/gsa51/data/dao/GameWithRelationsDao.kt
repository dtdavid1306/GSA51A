package com.golfapp.gsa51.data.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Query
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.data.entities.TeamPairing
import kotlinx.coroutines.flow.Flow

@Dao
interface GameWithRelationsDao {
    @Transaction
    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameWithRelations(gameId: Long): Flow<GameWithDetails>
}

data class GameWithDetails(
    @Embedded val game: Game,

    @Relation(
        parentColumn = "id",
        entityColumn = "gameId",
        entity = Score::class
    )
    val scores: List<Score>,

    @Relation(
        parentColumn = "id",
        entityColumn = "gameId",
        entity = TeamPairing::class
    )
    val teamPairings: List<TeamPairing>
)