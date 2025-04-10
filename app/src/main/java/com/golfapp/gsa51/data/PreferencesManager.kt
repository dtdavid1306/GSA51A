// Create a new file: PreferencesManager.kt in the data package
package com.golfapp.gsa51.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("gsa_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MAX_SCORE_LIMIT = "max_score_limit"
        private const val DEFAULT_MAX_SCORE_LIMIT = 15  // Default set to 15
    }

    fun saveMaxScoreLimit(limit: Int) {
        sharedPreferences.edit().putInt(KEY_MAX_SCORE_LIMIT, limit).apply()
    }

    fun getMaxScoreLimit(): Int {
        return sharedPreferences.getInt(KEY_MAX_SCORE_LIMIT, DEFAULT_MAX_SCORE_LIMIT)
    }
}