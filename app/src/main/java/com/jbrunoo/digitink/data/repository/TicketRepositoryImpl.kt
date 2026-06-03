package com.jbrunoo.digitink.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.jbrunoo.digitink.common.Constants
import com.jbrunoo.digitink.domain.model.Ticket
import com.jbrunoo.digitink.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : TicketRepository {
    override fun readTicket(): Flow<Ticket> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences.toTicket(now = System.currentTimeMillis())
        }

    override suspend fun refreshTickets(): Ticket {
        val now = System.currentTimeMillis()
        var ticket = Ticket()
        dataStore.edit { preferences ->
            ticket = preferences.toTicket(now)
            preferences[countKey] = ticket.count
            preferences[refillAtKey] = nextRefillAt(ticket, now)
        }
        return ticket
    }

    override suspend fun minusTickets(count: Int): Boolean {
        var isDeducted = false
        val now = System.currentTimeMillis()
        dataStore.edit { preferences ->
            val currentTicket = preferences.toTicket(now)
            if (currentTicket.count >= count) {
                val updatedCount = currentTicket.count - count
                preferences[countKey] = updatedCount
                preferences[refillAtKey] = if (updatedCount >= Constants.MAX_TICKET_COUNT) {
                    0L
                } else {
                    now
                }
                isDeducted = true
            }
        }
        return isDeducted
    }

    override suspend fun plusTickets(count: Int) {
        val now = System.currentTimeMillis()
        dataStore.edit { preferences ->
            val currentTicket = preferences.toTicket(now)
            val updatedCount = currentTicket.count + count
            preferences[countKey] = updatedCount
            preferences[refillAtKey] = if (updatedCount >= Constants.MAX_TICKET_COUNT) {
                0L
            } else {
                now
            }
        }
    }

    private fun Preferences.toTicket(now: Long): Ticket {
        val currentCount = this[countKey] ?: Constants.MAX_TICKET_COUNT
        if (currentCount >= Constants.MAX_TICKET_COUNT) {
            return Ticket(count = currentCount, millisUntilNextRefill = 0L)
        }

        val refillAt = this[refillAtKey] ?: now
        val elapsed = (now - refillAt).coerceAtLeast(0L)
        val earnedCount = (elapsed / Constants.TICKET_REFILL_INTERVAL_MILLIS).toInt()
        val updatedCount = (currentCount + earnedCount).coerceAtMost(Constants.MAX_TICKET_COUNT)

        if (updatedCount >= Constants.MAX_TICKET_COUNT) {
            return Ticket(count = updatedCount, millisUntilNextRefill = 0L)
        }

        val nextRefill = Constants.TICKET_REFILL_INTERVAL_MILLIS -
            (elapsed % Constants.TICKET_REFILL_INTERVAL_MILLIS)

        return Ticket(
            count = updatedCount,
            millisUntilNextRefill = nextRefill,
        )
    }

    private fun nextRefillAt(
        ticket: Ticket,
        now: Long,
    ): Long = if (ticket.count >= Constants.MAX_TICKET_COUNT) {
        0L
    } else {
        now + ticket.millisUntilNextRefill - Constants.TICKET_REFILL_INTERVAL_MILLIS
    }

    companion object {
        private val countKey = intPreferencesKey(Constants.TICKET_KEY)
        private val refillAtKey = longPreferencesKey(Constants.TICKET_REFILL_AT_KEY)
    }
}
