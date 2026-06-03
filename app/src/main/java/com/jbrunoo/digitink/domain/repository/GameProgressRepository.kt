package com.jbrunoo.digitink.domain.repository

import com.jbrunoo.digitink.domain.model.GameProgress
import kotlinx.coroutines.flow.Flow

interface GameProgressRepository {
    fun readGameProgress(): Flow<GameProgress>

    suspend fun addCoins(count: Int)

    suspend fun purchaseInfiniteLifeUpgrade(): Boolean

    suspend fun getInfiniteMaxLifeCount(): Int

    suspend fun incrementInfinitePlayCount(): Int
}
