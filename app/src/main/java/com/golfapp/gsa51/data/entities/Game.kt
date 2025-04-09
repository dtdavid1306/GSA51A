package com.golfapp.gsa51.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val location: String,
    val date: Date,
    val betUnit: Int,
    val startingHole: Int = 1, // New field with default value of 1
    val currentHole: Int = 1, // Add this new field
    val isCompleted: Boolean = false,
    val maxScoreLimit: Int = 10 // Add this new field with default value 10
)