package com.golfapp.gsa51.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 4, // Update version number (was 3)
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

        // Define migration from version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the maxScoreLimit column with a default value of 10
                database.execSQL("ALTER TABLE games ADD COLUMN maxScoreLimit INTEGER NOT NULL DEFAULT 10")
            }
        }

        fun getDatabase(context: Context): GSA51Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GSA51Database::class.java,
                    "gsa51_database"
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_3_4) // Add the migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}