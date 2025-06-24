package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.dataSource.remote.AuthRemoteDataSource
import com.jbrunoo.digitink.data.dataSource.remote.AuthRemoteDataSourceImpl
import com.jbrunoo.digitink.data.dataSource.remote.ScoreRemoteDataSource
import com.jbrunoo.digitink.data.dataSource.remote.ScoreRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(dataSourceImpl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindScoreRemoteDataSource(dataSourceImpl: ScoreRemoteDataSourceImpl): ScoreRemoteDataSource
}
