package com.example.quiziniapp.heplers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore("user_prefs")
    private val NAME_KEY = stringPreferencesKey("name")
    private val SCORE_KEY = intPreferencesKey("user_score")
    private val LAST_QUIZ_SCORE_KEY = intPreferencesKey("last_quiz_score")

    val ALL_CATEGORIES = listOf(
        "Knowledge",
        "Sports",
        "Education",
        "For Kids",
        "Tech & Games",
        "Culture & Society"
    )

    suspend fun saveName(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
        }
    }

    fun getName(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[NAME_KEY]
        }
    }

    suspend fun saveScore(context: Context, score: Int) {
        context.dataStore.edit { prefs ->
            prefs[SCORE_KEY] = score
        }
    }

    fun getScore(context: Context): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[SCORE_KEY] ?: 0
        }
    }

    suspend fun saveLastQuizScore(context: Context, score: Int) {
        context.dataStore.edit { prefs ->
            prefs[LAST_QUIZ_SCORE_KEY] = score
        }
    }

    fun getLastQuizScore(context: Context): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[LAST_QUIZ_SCORE_KEY] ?: 0
        }
    }

    fun getProgress(context: Context, category: String): Flow<Int> {
        val key = stringPreferencesKey("progress_${category.lowercase()}")
        return context.dataStore.data.map { prefs ->
            prefs[key]?.toIntOrNull() ?: 0
        }
    }

    suspend fun saveProgress(context: Context, category: String, solved: Int) {
        val catKey = stringPreferencesKey("progress_${category.lowercase()}")
        context.dataStore.edit { prefs ->
            val oldVal = prefs[catKey]?.toIntOrNull() ?: 0
            val newCategoryScore = maxOf(oldVal, solved)
            prefs[catKey] = newCategoryScore.toString()

            // Recalculate total score across all categories
            var newTotalScore = 0
            ALL_CATEGORIES.forEach { cat ->
                val k = stringPreferencesKey("progress_${cat.lowercase()}")
                val currentCategoryScore = if (cat.equals(category, ignoreCase = true)) {
                    newCategoryScore
                } else {
                    prefs[k]?.toIntOrNull() ?: 0
                }
                newTotalScore += currentCategoryScore
            }
            prefs[SCORE_KEY] = newTotalScore
        }
    }

    suspend fun saveCorrectAnswersCount(context: Context, count: Int) {
        context.dataStore.edit { prefs ->
            prefs[intPreferencesKey("correct_answers_count")] = count
        }
    }

    suspend fun resetAll(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[SCORE_KEY] = 0
            prefs[LAST_QUIZ_SCORE_KEY] = 0
            prefs[intPreferencesKey("correct_answers_count")] = 0
            ALL_CATEGORIES.forEach { category ->
                val key = stringPreferencesKey("progress_${category.lowercase()}")
                prefs[key] = "0"
            }
        }
    }
}
