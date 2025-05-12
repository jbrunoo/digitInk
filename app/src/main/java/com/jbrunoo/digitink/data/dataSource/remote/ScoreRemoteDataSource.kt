package com.jbrunoo.digitink.data.dataSource.remote

interface ScoreRemoteDataSource {

    fun submitRemoteScore(leaderBoardKey: String, score: Long)
}
