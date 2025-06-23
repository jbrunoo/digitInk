package com.jbrunoo.digitink.domain.repository

import com.jbrunoo.digitink.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun readTicket(): Flow<Ticket>

    suspend fun minusTickets(count: Int)

    suspend fun plusTickets(count: Int)
}
