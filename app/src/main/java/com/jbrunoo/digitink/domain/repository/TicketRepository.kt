package com.jbrunoo.digitink.domain.repository

import com.jbrunoo.digitink.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun readTicket(): Flow<Ticket>

    suspend fun refreshTickets(): Ticket

    suspend fun minusTickets(count: Int): Boolean

    suspend fun plusTickets(count: Int)
}
