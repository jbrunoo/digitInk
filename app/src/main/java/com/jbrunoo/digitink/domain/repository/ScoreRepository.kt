package com.jbrunoo.digitink.domain.repository

import com.jbrunoo.digitink.domain.model.Score
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun readLocalScore(): Flow<Score>

    suspend fun saveLocalScore(dataStoreKey: String, score: Long)

    suspend fun clearLocalScore()

    fun showLeaderBoard()

    fun submitRemoteScore()
}
