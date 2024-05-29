package com.jbrunoo.digitink.domain

import com.jbrunoo.digitink.utils.GameResultKey
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    suspend fun saveResult(key: GameResultKey, value: String)

    fun readResult(key: GameResultKey): Flow<String>
}