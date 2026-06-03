package com.jbrunoo.digitink.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.domain.model.GameProgress
import com.jbrunoo.digitink.domain.repository.GameProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class GameProgressRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : GameProgressRepository {
    override fun readGameProgress(): Flow<GameProgress> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences.toGameProgress()
        }

    override suspend fun addCoins(count: Int) {
        if (count <= 0) return

        dataStore.edit { preferences ->
            val currentCoinCount = preferences[coinKey] ?: 0
            preferences[coinKey] = currentCoinCount + count
        }
    }

    override suspend fun purchaseInfiniteLifeUpgrade(): Boolean {
        var isPurchased = false

        dataStore.edit { preferences ->
            val progress = preferences.toGameProgress()
            val cost = progress.nextInfiniteLifeUpgradeCost ?: return@edit

            if (progress.coinCount >= cost) {
                preferences[coinKey] = progress.coinCount - cost
                preferences[infiniteMaxLifeKey] = progress.infiniteMaxLifeCount + 1
                isPurchased = true
            }
        }

        return isPurchased
    }

    override suspend fun getInfiniteMaxLifeCount(): Int =
        dataStore.data.first().toGameProgress().infiniteMaxLifeCount

    override suspend fun incrementInfinitePlayCount(): Int {
        var updatedCount = 0

        dataStore.edit { preferences ->
            updatedCount = (preferences[infinitePlayCountKey] ?: 0) + 1
            preferences[infinitePlayCountKey] = updatedCount
        }

        return updatedCount
    }

    private fun Preferences.toGameProgress(): GameProgress {
        val infiniteMaxLifeCount = (this[infiniteMaxLifeKey] ?: Constants.DEFAULT_INFINITE_LIFE_COUNT)
            .coerceIn(Constants.DEFAULT_INFINITE_LIFE_COUNT, Constants.MAX_INFINITE_LIFE_COUNT)

        return GameProgress(
            coinCount = this[coinKey] ?: 0,
            infiniteMaxLifeCount = infiniteMaxLifeCount,
        )
    }

    companion object {
        private val coinKey = intPreferencesKey(Constants.COIN_KEY)
        private val infiniteMaxLifeKey = intPreferencesKey(Constants.INFINITE_MAX_LIFE_KEY)
        private val infinitePlayCountKey = intPreferencesKey(Constants.DATASTORE_KEY_INFINITE_PLAY_COUNT)
    }
}
