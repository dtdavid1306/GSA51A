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
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // Initialize with the stored value, not hardcoded 10
    var maxScoreLimit by mutableStateOf(preferencesManager.getMaxScoreLimit())
        private set

    // Add currency symbol state
    var currencySymbol by mutableStateOf(preferencesManager.getCurrencySymbol())
        private set

    // Available currency symbols
    val availableCurrencySymbols = listOf("$", "€", "£", "¥", "₹", "GSA_LOGO")

    var isLoading by mutableStateOf(false)
        private set

    var saveSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun updateMaxScoreLimit(limit: Int) {
        maxScoreLimit = limit
    }

    // Add method to update currency symbol
    fun updateCurrencySymbol(symbol: String) {
        currencySymbol = symbol
    }

    fun saveSettings(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("AdvSettings", "Saving limit: $maxScoreLimit")
                Log.d("AdvSettings", "Saving currency: $currencySymbol")

                // Save to SharedPreferences
                preferencesManager.saveMaxScoreLimit(maxScoreLimit)
                preferencesManager.saveCurrencySymbol(currencySymbol)

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