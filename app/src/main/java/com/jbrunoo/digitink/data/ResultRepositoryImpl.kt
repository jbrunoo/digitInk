package com.jbrunoo.digitink.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ResultRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) : ResultRepository {
    override suspend fun saveValue(dataStoreKey: String, score: Long) {
        dataStore.edit { settings ->
            val key = longPreferencesKey(dataStoreKey)
            val currentValue = settings[key] ?: 0
            if (score > currentValue) settings[key] = score
        }
    }

    override fun readResult(): Flow<Result> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Result(
                    normalMode5 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_5)] ?: 0,
                    normalMode10 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_10)] ?: 0,
                    normalMode15 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_15)] ?: 0,
                    normalMode20 = preferences[longPreferencesKey(Constants.DATASTORE_KEY_20)] ?: 0,
                    infiniteMode = preferences[longPreferencesKey(Constants.DATASTORE_KEY_INFINITE)] ?: 0,
                )
            }
    }

    override suspend fun clearResult() {
        dataStore.edit {
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_5))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_10))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_15))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_20))
            it.remove(longPreferencesKey(Constants.DATASTORE_KEY_INFINITE))
        }
    }
}
