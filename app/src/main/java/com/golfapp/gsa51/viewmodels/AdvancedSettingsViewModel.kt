// Update AdvancedSettingsViewModel.kt to use PreferencesManager

package com.golfapp.gsa51.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfapp.gsa51.data.PreferencesManager
import com.golfapp.gsa51.repositories.GolfRepository
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    private val repository: GolfRepository,
    private val preferencesManager: PreferencesManager // Add this parameter
) : ViewModel() {

    // Initialize with the stored value, not hardcoded 10
    var maxScoreLimit by mutableStateOf(preferencesManager.getMaxScoreLimit())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var saveSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun updateMaxScoreLimit(limit: Int) {
        maxScoreLimit = limit
    }

    fun saveSettings(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("AdvSettings", "Saving limit: $maxScoreLimit")

                // Save to SharedPreferences
                preferencesManager.saveMaxScoreLimit(maxScoreLimit)

                saveSuccessMessage = "Settings saved successfully"

                // Clear the success message after 2 seconds
                kotlinx.coroutines.delay(2000)
                saveSuccessMessage = null

                onComplete()
            } catch (e: Exception) {
                // Handle error
                println("Error saving settings: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}