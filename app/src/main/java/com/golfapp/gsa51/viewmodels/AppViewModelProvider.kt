// In AppViewModelProvider.kt
package com.golfapp.gsa51.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.golfapp.gsa51.GSA51Application
import com.golfapp.gsa51.repositories.GolfRepository
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import kotlinx.coroutines.delay

// Add this key definition
//object GameIdKey : CreationExtras.Key<Long> {
    //override val name: String = "GameId"
//}

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            GameDetailsViewModel(
                repository = getApplication().container.golfRepository
            )
        }
// Add this initializer to the Factory
        initializer {
            ScoringViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
        initializer {
            SavedGamesViewModel(
                repository = getApplication().container.golfRepository
            )
        }
        initializer {
            ResultsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
        initializer {
            ResultsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
        initializer {
            IndividualGameSettingsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
        initializer {
            TeamPairingViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
        initializer {
            FinalScoreDetailsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L  // Default value, will be set via initialize
            )
        }
    }
}


/**
 * Extension function to queries for [Application] object and returns an instance of
 * [GSA51Application].
 */
fun CreationExtras.getApplication(): GSA51Application =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GSA51Application)