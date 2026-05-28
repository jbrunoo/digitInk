package com.jbrunoo.digitink.domain.model

data class Ticket(
    val count: Int = 0,
    val millisUntilNextRefill: Long = 0L,
)
