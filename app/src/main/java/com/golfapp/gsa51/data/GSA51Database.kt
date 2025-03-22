package com.golfapp.gsa51.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.golfapp.gsa51.data.dao.GameDao
import com.golfapp.gsa51.data.dao.GameWithRelationsDao
import com.golfapp.gsa51.data.dao.PlayerDao
import com.golfapp.gsa51.data.dao.ScoreDao
import com.golfapp.gsa51.data.dao.TeamPairingDao
import com.golfapp.gsa51.data.entities.Game
import com.golfapp.gsa51.data.entities.Player
import com.golfapp.gsa51.data.entities.Score
import com.golfapp.gsa51.data.entities.TeamPairing

@Database(
    entities = [
        Player::class,
        Game::class,
        Score::class,
        TeamPairing::class
    ],
    version = 3, // Update version number (was 2)
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GSA51Database : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun scoreDao(): ScoreDao
    abstract fun teamPairingDao(): TeamPairingDao
    abstract fun gameWithRelationsDao(): GameWithRelationsDao

    companion object {
        @Volatile
        private var INSTANCE: GSA51Database? = null

        fun getDatabase(context: Context): GSA51Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GSA51Database::class.java,
                    "gsa51_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}