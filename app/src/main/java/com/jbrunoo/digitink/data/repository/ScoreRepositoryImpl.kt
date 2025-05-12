package com.jbrunoo.digitink.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import com.jbrunoo.digitink.domain.model.Score
import com.jbrunoo.digitink.common.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ScoreRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) : ScoreRepository {
    override fun readLocalScore(): Flow<Score> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Score(
                    normalMode5 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_5)] ?: 0,
                    normalMode10 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_10)] ?: 0,
                    normalMode15 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_15)] ?: 0,
                    normalMode20 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_20)] ?: 0,
                    infiniteMode = preferences[longPreferencesKey(Constants.DATASTORE_KEY_INFINITE)] ?: 0,
                )
            }
    }

    override suspend fun saveLocalScore(dataStoreKey: String, score: Long) {
        dataStore.edit { settings ->
            val key = longPreferencesKey(dataStoreKey)
            val currentValue = settings[key] ?: 0
            if (score > currentValue) settings[key] = score
        }
    }

    override suspend fun clearLocalScore() {
        dataStore.edit {
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_5))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_10))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_15))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_20))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_INFINITE))
        }
    }

    override fun showLeaderBoard() {
        TODO("Not yet implemented")
    }

    override fun submitRemoteScore() {
        TODO("Not yet implemented")
    }
}
