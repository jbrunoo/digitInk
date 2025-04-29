package com.jbrunoo.digitink.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.jbrunoo.digitink.domain.TicketRepository
import com.jbrunoo.digitink.domain.model.Ticket
import com.jbrunoo.digitink.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class TicketRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) : TicketRepository {

    override fun readTicket(): Flow<Ticket> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Ticket(
                    count = preferences[key] ?: 0,
                )
            }
    }

    override suspend fun minusTickets(count: Int) {
        dataStore.edit { preferences ->
            val currentValue = preferences[key] ?: 0
            if (currentValue != 0) preferences[key] = currentValue - count
        }
    }

    override suspend fun plusTickets(count: Int) {
        dataStore.edit { preferences ->
            val currentValue = preferences[key] ?: 0
            if (currentValue < 3) preferences[key] = currentValue + count
        }
    }

    companion object {
        private val key = intPreferencesKey(Constants.TICKET_KEY)
    }
}
