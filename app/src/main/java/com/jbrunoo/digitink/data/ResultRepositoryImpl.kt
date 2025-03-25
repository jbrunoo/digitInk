package com.jbrunoo.digitink.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.utils.GameResultKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResultRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) : ResultRepository {
    override suspend fun saveValue(gameResultKey: GameResultKey, score: Long) {
        dataStore.edit { settings ->
            val key = longPreferencesKey(gameResultKey.key)
            val currentValue = settings[key] ?: 0
            if (score > currentValue) settings[key] = score
        }
    }

    override fun readResult(): Flow<Result> {
        return dataStore.data.map { preferences ->
            Result(
                speedGame5 = preferences[longPreferencesKey(GameResultKey.SPEED_GAME_5.key)] ?: 0,
                speedGame10 = preferences[longPreferencesKey(GameResultKey.SPEED_GAME_10.key)] ?: 0,
                speedGame15 = preferences[longPreferencesKey(GameResultKey.SPEED_GAME_15.key)] ?: 0,
                speedGame20 = preferences[longPreferencesKey(GameResultKey.SPEED_GAME_20.key)] ?: 0
            )
        }
    }

    override suspend fun clearResult() {
        dataStore.edit {
            it.clear()
        }
    }
}
