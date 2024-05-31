package com.jbrunoo.digitink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.utils.Constants.GAME_RESULT
import com.jbrunoo.digitink.utils.GameResultKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultRepositoryImpl(
    private val context: Context
): ResultRepository {
    override suspend fun saveValue(gameResultKey: GameResultKey, value: Int) {
        context.dataStore.edit { settings ->
            val key = stringPreferencesKey(gameResultKey.key)
            val currentValue = settings[key] ?: "0"
            if(value > currentValue.toInt()) settings[key] = value.toString()
        }
    }

    override fun readResult(): Flow<Result> {
        return context.dataStore.data.map { preferences ->
            Result(
                speedGame5 = preferences[stringPreferencesKey(GameResultKey.SPEED_GAME_5.key)] ?: "no-result",
                speedGame10 = preferences[stringPreferencesKey(GameResultKey.SPEED_GAME_10.key)] ?: "no-result",
                speedGame15 = preferences[stringPreferencesKey(GameResultKey.SPEED_GAME_15.key)] ?: "no-result",
                speedGame20 = preferences[stringPreferencesKey(GameResultKey.SPEED_GAME_20.key)] ?: "no-result"
            )
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = GAME_RESULT)