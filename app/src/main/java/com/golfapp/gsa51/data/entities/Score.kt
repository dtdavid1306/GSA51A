package com.golfapp.gsa51.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "scores",
    primaryKeys = ["gameId", "playerId", "holeNumber"],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("gameId"),
        Index("playerId")
    ]
)
data class Score(
    val gameId: Long,
    val playerId: Long,
    val holeNumber: Int, // 1-18
    val score: Int,
    val par: Int = 4 // Default par value of 4
)