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

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            GameDetailsViewModel(
                repository = getApplication().container.golfRepository,
                preferencesManager = getApplication().container.preferencesManager  // Add this
            )
        }

        initializer {
            ScoringViewModel(
                repository = getApplication().container.golfRepository,
                preferencesManager = getApplication().container.preferencesManager,
                gameId = 0L
            )
        }

        initializer {
            SavedGamesViewModel(
                repository = getApplication().container.golfRepository
            )
        }

        // In AppViewModelProvider.kt, update the ResultsViewModel initializer
        initializer {
            ResultsViewModel(
                repository = getApplication().container.golfRepository,
                preferencesManager = getApplication().container.preferencesManager, // Add this
                gameId = 0L
            )
        }

        initializer {
            IndividualGameSettingsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L
            )
        }

        initializer {
            TeamPairingViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L
            )
        }

        initializer {
            AdvancedSettingsViewModel(
                repository = getApplication().container.golfRepository,
                preferencesManager = getApplication().container.preferencesManager
            )
        }
// Update the ResultsViewModel initializer in AppViewModelProvider.kt
        initializer {
            ResultsViewModel(
                repository = getApplication().container.golfRepository,
                preferencesManager = getApplication().container.preferencesManager, // Add this
                gameId = 0L
            )
        }
        initializer {
            FinalScoreDetailsViewModel(
                repository = getApplication().container.golfRepository,
                gameId = 0L
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