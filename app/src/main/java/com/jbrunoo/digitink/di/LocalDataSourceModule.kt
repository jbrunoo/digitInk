package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.dataSource.local.ClassifierLocalDataSource
import com.jbrunoo.digitink.data.dataSource.local.ClassifierLocalDataSourceImpl
import com.jbrunoo.digitink.data.dataSource.local.ScoreLocalDataSource
import com.jbrunoo.digitink.data.dataSource.local.ScoreLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindClassifierLocalDataSource(dataSourceImpl: ClassifierLocalDataSourceImpl): ClassifierLocalDataSource

    @Binds
    @Singleton
    abstract fun bindScoreLocalDataSource(dataSourceImpl: ScoreLocalDataSourceImpl): ScoreLocalDataSource
}
