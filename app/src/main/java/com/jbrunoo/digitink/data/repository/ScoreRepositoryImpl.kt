package com.jbrunoo.digitink.data.repository

import com.jbrunoo.digitink.data.dataSource.local.ScoreLocalDataSource
import com.jbrunoo.digitink.domain.model.Score
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScoreRepositoryImpl @Inject constructor(
    private val scoreLocalDataSource: ScoreLocalDataSource,
) : ScoreRepository {

    override fun readLocalScore(): Flow<Score> = scoreLocalDataSource.readLocalScore()

    override suspend fun saveLocalScore(
        dataStoreKey: String,
        score: Long,
    ) {
        scoreLocalDataSource.saveLocalScore(dataStoreKey, score)
    }

    override suspend fun clearLocalScore() {
        scoreLocalDataSource.clearLocalScore()
    }
}
