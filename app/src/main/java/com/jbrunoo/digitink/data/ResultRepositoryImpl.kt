package com.jbrunoo.digitink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.utils.Constants.GAME_RESULT
import com.jbrunoo.digitink.utils.GameResultKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResultRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResultRepository {
    override suspend fun saveValue(gameResultKey: GameResultKey, value: Int) {
        context.dataStore.edit { settings ->
            val key = intPreferencesKey(gameResultKey.key)
            val currentValue = settings[key] ?: 0
            if (value > currentValue) settings[key] = value
        }
    }

    override fun readResult(): Flow<Result> {
        return context.dataStore.data.map { preferences ->
            Result(
                speedGame5 = preferences[intPreferencesKey(GameResultKey.SPEED_GAME_5.key)] ?: 0,
                speedGame10 = preferences[intPreferencesKey(GameResultKey.SPEED_GAME_10.key)] ?: 0,
                speedGame15 = preferences[intPreferencesKey(GameResultKey.SPEED_GAME_15.key)] ?: 0,
                speedGame20 = preferences[intPreferencesKey(GameResultKey.SPEED_GAME_20.key)] ?: 0
            )
        }
    }

    override suspend fun clearResult() {
        context.dataStore.edit {
            it.clear()
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = GAME_RESULT)