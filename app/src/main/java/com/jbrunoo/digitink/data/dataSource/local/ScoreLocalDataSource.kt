package com.jbrunoo.digitink.data.dataSource.local

interface ScoreLocalDataSource {

    fun readLocalScore()

    fun saveLocalScore()

    fun clearLocalScore()
}
