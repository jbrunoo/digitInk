package com.jbrunoo.digitink.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.utils.Constants.GAME_RESULT
import com.jbrunoo.digitink.utils.GameResultKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultRepositoryImpl(
    private val context: Context
): ResultRepository {
    override suspend fun saveResult(key: GameResultKey, value: String) {
        context.dataStore.edit { settings ->
            settings[stringPreferencesKey(key.key)] = value
        }
    }

    override fun readResult(key: GameResultKey): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key.key)] ?: "no-result"
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = GAME_RESULT)

