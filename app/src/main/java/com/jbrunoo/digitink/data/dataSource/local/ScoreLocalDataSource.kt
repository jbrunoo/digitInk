package com.jbrunoo.digitink.data.dataSource.local

import com.jbrunoo.digitink.domain.model.Score
import kotlinx.coroutines.flow.Flow

interface ScoreLocalDataSource {
    fun readLocalScore(): Flow<Score>

    suspend fun saveLocalScore(dataStoreKey: String, score: Long)

    suspend fun clearLocalScore()
}
