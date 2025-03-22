package com.golfapp.gsa51.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val participateInIndividualGame: Boolean = true
)