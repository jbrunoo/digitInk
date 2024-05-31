package com.jbrunoo.digitink.domain

import com.jbrunoo.digitink.domain.model.Result
import com.jbrunoo.digitink.utils.GameResultKey
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    suspend fun saveValue(gameResultKey: GameResultKey, value: Int)

    fun readResult(): Flow<Result>
}