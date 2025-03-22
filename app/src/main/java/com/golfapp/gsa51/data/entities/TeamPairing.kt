package com.golfapp.gsa51.data.entities
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "team_pairings",
    primaryKeys = ["gameId", "section"],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeamPairing(
    val gameId: Long,
    val section: Int, // 1, 2, or 3 (representing holes 1-6, 7-12, 13-18)
    val team1Player1Id: Long,
    val team1Player2Id: Long,
    val team2Player1Id: Long,
    val team2Player2Id: Long
)