package com.jbrunoo.digitink.data.dataSource.remote

import com.google.android.gms.games.LeaderboardsClient
import javax.inject.Inject

class ScoreRemoteDataSourceImpl @Inject constructor(
    private val leaderboardsClient: LeaderboardsClient,
) : ScoreRemoteDataSource {

    override fun submitRemoteScore(
        leaderBoardKey: String,
        score: Long,
    ) = leaderboardsClient.submitScore(leaderBoardKey, score)
}
