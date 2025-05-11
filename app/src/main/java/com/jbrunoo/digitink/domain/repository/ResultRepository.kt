package com.jbrunoo.digitink.domain.repository

import com.jbrunoo.digitink.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    suspend fun saveValue(dataStoreKey: String, score: Long)

    fun readResult(): Flow<Result>

    suspend fun clearResult()
}
